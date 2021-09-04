import React, {useEffect, useState} from "react";
import {backUrl} from "../backend";

interface BannerEditorProps {
    bannerId: number|null, // id of selected banner to edit
    changeSelectedId: (id: number|null) => void, // Callback for changing selected ID to edit and display
    // Function to call after saving or deleting entity to update a list of entities
    listUpdater: (setter: (b: boolean) => boolean) => void // call (prev => !prev)
}

function BannerEditor(props: BannerEditorProps) {
    const [name, setName] = useState<string>("");
    const [price, setPrice] = useState<number>(0.0);
    const [content, setContent] = useState<string>("");
    const [categoryId, setCategoryId] = useState<number>(0);
    // list of categories to select from
    const [categories, setCategories] = useState<{id: number, name: string}[]>([]);
    // form fields post errors
    const [inputErrors, setInputErrors] = useState<any>({})
    // Text message of an error if there was an error
    const [error, setError] = useState<string|null>(null);
    // Info/submit message
    const [message, setMessage] = useState<string|null>(null);

    useEffect(() => {
        setError(null);
        setInputErrors({});
        setMessage(null);
        if (props.bannerId !== null) {
            fetch(backUrl + '/banners/' + props.bannerId)
                .then(response => {
                    if (response.status === 404) {
                        props.changeSelectedId(null);
                        return Promise.reject("Banner with such ID does not exist")
                    }
                    if (!response.ok) {
                        return Promise.reject("Error occurred, try to refresh a page")
                    }
                    return response.json();
                })
                .then(data => {
                    setName(data.name)
                    setPrice(data.price)
                    setCategoryId(data.category.id)
                    setContent(data.content)
                }, error => {
                    setError(error);
                })
        } else {
            setName("");
            setPrice(0);
            setContent("");
            setCategoryId(0)
        }
    },[props.bannerId]);

    useEffect(() => {
        const getCategoriesList = async () => {
            setCategories([]);
            const pageSize = 5;
            const pagesResponse = await fetch(backUrl + "/categories/pages?pageSize=" + pageSize);
            const pages = await pagesResponse.text();
            for (let i = 0; i < parseInt(pages); ++i){
                const response = await fetch(backUrl + "/categories/list?pageSize=" + pageSize + "&page=" + i);
                const nextList = await response.json();
                setCategories(c => c.concat(nextList.map((e: any) => {return {id: e.id, name: e.name}})));
                setCategoryId(id => Number(nextList[0].id));
            }
        };
        getCategoriesList();
    }, [props.bannerId]);

    const deleteHandler = () => {
        if (props.bannerId === null) {
            return;
        }
        const requestOptions: RequestInit = {
            method: 'DELETE'
        };
        fetch(backUrl + '/banners/' + props.bannerId, requestOptions)
            .then(response => {
                setError(null);
                setInputErrors({});
                setMessage(null);
                if (response.status === 204) {
                    return response.text();
                }
                return Promise.reject({type: "content", data: "Error occurred, try to refresh a page"});
            })
            .then(message => {
                setMessage(message);
                props.changeSelectedId(null);
                props.listUpdater(prev => !prev);
                setName("");
                setPrice(0);
                setContent("");
                setCategoryId(categories[0].id)
            }, error => {
                setError(error.data)
            });
    };

    const saveHandler = () => {
        const preparedBanner = {id: props.bannerId, name: name, price: price, category: {id: categoryId}, content: content};
        console.log(preparedBanner);
        const requestOptions: RequestInit = {
            method: preparedBanner.id === null ? 'POST' : 'PUT',
            headers: { 'Content-Type': 'application/json'},
            body: JSON.stringify(preparedBanner)
        };
        fetch(backUrl + '/banners', requestOptions)
            .then(async response => {
                setError(null);
                setInputErrors({});
                setMessage(null);
                if (response.ok) {
                    return response.text();
                }
                if (response.status === 400) {
                    return Promise.reject({type: "input_errors", data: await response.json()});
                }
                if (response.status === 409) {
                    return Promise.reject({type: "content", data: await response.text()});
                }
                return Promise.reject({type: "content", data: "Error occurred, try to refresh a page"});
            })
            .then(message => {
                setMessage(message);
                props.listUpdater(prev => !prev);
                if (props.bannerId === null) {
                    setName("");
                    setPrice(0);
                    setContent("");
                    setCategoryId(categories[0].id);
                }
            }, error => {
                if (error.type === "content") {
                    setError(error.data);
                }
                if (error.type === "input_errors") {
                    setInputErrors(error.data);
                }
            });
    };

    return (
        <div>
            <div className="editor-header">
                {props.bannerId === null ?
                    "Create new banner" :
                    name + "  ID: " + props.bannerId}
            </div>
            <div className="editor-form">
                <table>
                    <tr>
                        <td className="editor-field-name">Name</td>
                        <td className="editor-field">
                            <input type="text" value={name} onChange={event => setName(event.target.value)}/>
                        </td>
                        <td className="editor-field-error">
                            {inputErrors.name !== undefined ? inputErrors.name : ""}
                        </td>
                    </tr>
                    <tr>
                        <td className="editor-field-name">Price</td>
                        <td className="editor-field">
                            <input type="number" value={price} min={0.0} step={0.01}
                                   onChange={event => setPrice(Number(event.target.value))}/>
                        </td>
                        <td className="editor-field-error">
                            {inputErrors.price !== undefined ? inputErrors.price : ""}
                        </td>
                    </tr>
                    <tr>
                        <td className="editor-field-name">Category</td>
                        <td className="editor-field">
                            <select value={categoryId}
                                    onChange={event => setCategoryId(Number(event.target.value))}>
                                {categories.map((category) => {
                                    return (
                                        <option key={category.id} value={category.id}>
                                            {category.name}
                                        </option>
                                    );
                                })}
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td className="editor-field-name">Text</td>
                        <td className="editor-field">
                            <textarea value={content} onChange={event => setContent(event.target.value)}/>
                        </td>
                        <td className="editor-field-error">
                            {inputErrors.content !== undefined ? inputErrors.content : ""}
                        </td>
                    </tr>
                </table>
            </div>
            <div className="editor-notifier">
                {error !== null ?
                    (
                        <div className="editor-error">
                            {error}
                        </div>
                    ) : ""}
                {message !== null ?
                    (
                        <div className="editor-message">
                            {message}
                        </div>
                    ) : ""}
            </div>
            <div className="editor-buttons">
                <button className="save-button" onClick={saveHandler}>
                    Save
                </button>
                {props.bannerId !== null ?
                    (<button className="delete-button" onClick={deleteHandler}>
                        Delete
                    </button>) : ""
                }
            </div>
        </div>
    )
}

export default BannerEditor;
