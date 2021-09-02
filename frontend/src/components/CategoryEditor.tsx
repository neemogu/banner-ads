import React, {useEffect, useState} from "react";
import {backUrl} from "../backend";

interface CategoryEditorProps {
    categoryId: number|null,
    changeSelectedId: (id: number|null) => void,
    listUpdater: (setter: (b: boolean) => boolean) => void
}

function CategoryEditor(props: CategoryEditorProps) {
    const [name, setName] = useState<string>("");
    const [reqName, setReqName] = useState<string>("");
    const [inputErrors, setInputErrors] = useState<any>({})
    const [error, setError] = useState<string|null>(null);
    const [message, setMessage] = useState<string|null>(null);

    useEffect(() => {
        setError(null);
        setInputErrors({});
        setMessage(null);
        if (props.categoryId !== null) {
            fetch(backUrl + '/categories/' + props.categoryId)
                .then(response => {
                    if (response.status === 404) {
                        props.changeSelectedId(null);
                        return Promise.reject("Category with such ID does not exist")
                    }
                    if (!response.ok) {
                        return Promise.reject("Error occurred, try to refresh a page")
                    }
                    return response.json();
                })
                .then(data => {
                    setName(data.name)
                    setReqName(data.reqName)
                }, error => {
                    setError(error);
                })
        } else {
            setName("");
            setReqName("");
        }
    },[props.categoryId]);

    const deleteHandler = () => {
        if (props.categoryId === null) {
            return;
        }
        const requestOptions: RequestInit = {
            method: 'DELETE'
        };
        fetch(backUrl + '/categories/' + props.categoryId, requestOptions)
            .then(async response => {
                setError(null);
                setInputErrors({});
                setMessage(null);
                if (response.status === 204) {
                    return response.text();
                }
                if (response.status === 409) {
                    return Promise.reject({type: "text", data: await response.text()});
                }
                return Promise.reject({type: "text", data: "Error occurred, try to refresh a page"});
            })
            .then(message => {
                setMessage(message);
                props.changeSelectedId(null);
                props.listUpdater(prev => !prev);
                setName("");
                setReqName("");
            }, error => {
                setError(error.data)
            });
    };

    const saveHandler = () => {
        const preparedCategory = {id: props.categoryId, name: name, reqName: reqName};
        console.log(JSON.stringify(preparedCategory));
        const requestOptions: RequestInit = {
            method: preparedCategory.id === null ? 'POST' : 'PUT',
            headers: { 'Content-Type': 'application/json'},
            body: JSON.stringify(preparedCategory)
        };
        fetch(backUrl + '/categories', requestOptions)
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
                    return Promise.reject({type: "text", data: await response.text()});
                }
                return Promise.reject({type: "text", data: "Error occurred, try to refresh a page"});
            })
            .then(message => {
                setMessage(message);
                props.changeSelectedId(props.categoryId);
                props.listUpdater(prev => !prev);
                if (props.categoryId === null) {
                    setName("");
                    setReqName("");
                }
            }, error => {
                console.log(error);
                if (error.type === "text") {
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
                {props.categoryId === null ?
                    "Create new category" :
                    name + "  ID: " + props.categoryId}
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
                        <th className="editor-field-name">Request ID</th>
                        <th className="editor-field">
                            <input type="text" value={reqName} onChange={event => setReqName(event.target.value)}/>
                            <span className="editor-field-error">
                                {inputErrors.reqName !== undefined ? inputErrors.reqName : ""}
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
                {props.categoryId !== null ?
                    (<button className="delete-button" onClick={deleteHandler}>
                        Delete
                    </button>) : ""
                }
            </div>
        </div>
    );
}

export default CategoryEditor;
