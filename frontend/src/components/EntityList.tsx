import React from "react";

interface EntityListProps {
    listData: {id: number, name: string}[],
    selectListElementHandler: (selectedId: number) => void,
    selectedId: number|null,
    error: any,
    isLoaded: boolean
}

function EntityList(props: EntityListProps) {
    if (!props.isLoaded) {
        return (
            <h1 className="loading">
                Loading...
            </h1>
        );
    }
    if (props.error) {
        return (
            <h3>
                Error occurred, try to refresh a page
            </h3>
        );
    }
    return (
        <div className="entity-list">
            <ul>
                {props.listData.map((entity) => {
                    return (
                        <li onClick={() => props.selectListElementHandler(entity.id)}
                            key={entity.id}
                            className={props.selectedId === entity.id ? "selected-entity" : "not-selected-entity"}>
                            {entity.name}
                        </li>
                    )
                })}
            </ul>
        </div>
    )
}

export default EntityList;
