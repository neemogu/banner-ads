import React from "react";
import {EntityType} from "../util/EntityType";
import BannerEditor from "./BannerEditor";
import CategoryEditor from "./CategoryEditor";

interface EntityEditorProps {
    entityId: number|null // ID of entity to edit and display
    entityType: EntityType, // type of entity to edit
    changeSelectedId: (id: number|null) => void // Callback for changing selected ID to edit and display
    // Function to call after saving or deleting entity to update a list of entities
    listUpdater: (setter: (b: boolean) => boolean) => void // call (prev => !prev)
}

function EntityEditor(props: EntityEditorProps) {
    return (
        <div className="entity-editor">
            {props.entityType === "banner" ?
                <BannerEditor bannerId={props.entityId}
                              changeSelectedId={props.changeSelectedId}
                              listUpdater={props.listUpdater}/> :
                <span/>}
            {props.entityType === "category" ?
                <CategoryEditor categoryId={props.entityId}
                                changeSelectedId={props.changeSelectedId}
                                listUpdater={props.listUpdater}/> :
                <span/>}
        </div>
    );
}

export default EntityEditor;
