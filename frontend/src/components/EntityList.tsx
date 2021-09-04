import React from "react";

interface EntityListProps {
    listData: {id: number, name: string}[], // list to display
    selectListElementHandler: (selectedId: number) => void, // change ID of selected entity to highlight callback
    selectedId: number|null, // ID of selected entity to highlight
    error: any, // if any error occurred while loading a list
    isLoaded: boolean // is list loaded
}

function EntityList(props: EntityListProps) {
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
