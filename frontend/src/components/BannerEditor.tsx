import React, {useEffect, useState} from "react";
import {backUrl} from "../backend";

interface BannerEditorProps {
    bannerId: number|null,
    changeSelectedId: (id: number|null) => void
}

function BannerEditor(props: BannerEditorProps) {
    const [name, setName] = useState<string>("");
    const [price, setPrice] = useState<number>(0.0);
    const [text, setText] = useState<string>("");
    const [categoryId, setCategoryId] = useState<number>(0);
    const [categories, setCategories] = useState<{id: number, name: string}[]>([{id: 0, name: "None"}]);
    const [inputErrors, setInputErrors] = useState<any>({})
    const [error, setError] = useState<string|null>(null);
    const [message, setMessage] = useState<string|null>(null);

    useEffect(() => {

    }, []);

    useEffect(() => {
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
                    setText(data.text)
                }, error => {
                    setError(error);
                })
        }
    },[props.bannerId]);

    const deleteHandler = () => {

    };

    const saveHandler = () => {

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
                        <th className="editor-field-name">Name</th>
                        <th className="editor-field">
                            <input type="text" value={name} onChange={event => setName(event.target.value)}/>
                            <span className="editor-field-error">
                                {inputErrors.name !== undefined ? inputErrors.name : ""}
                            </span>
                        </th>
                    </tr>
                    <tr>
                        <th className="editor-field-name">Price</th>
                        <th className="editor-field">
                            <input type="number" value={price} min={0} step={0.1}
                                   onChange={event => setPrice(Number(event.target.value))}/>
                            <span className="editor-field-error">
                                {inputErrors.price !== undefined ? inputErrors.price : ""}
                            </span>
                        </th>
                    </tr>
                    <tr>
                        <th className="editor-field-name">Category</th>
                        <th className="editor-field">
                            <select value={categoryId}
                                    onChange={event => setCategoryId(Number(event.target.value))}>
                                {categories.map((category) => {
                                    return (
                                        <option value={category.id}>
                                            {category.name}
                                        </option>
                                    );
                                })}
                            </select>
                        </th>
                    </tr>
                    <tr>
                        <th className="editor-field-name">Text</th>
                        <th className="editor-field">
                            <textarea value={text} rows={4} cols={60}
                                   onChange={event => setText(event.target.value)}/>
                            <span className="editor-field-error">
                                {inputErrors.text !== undefined ? inputErrors.text : ""}
                            </span>
                        </th>
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
