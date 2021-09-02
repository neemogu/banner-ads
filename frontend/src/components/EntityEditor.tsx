import React from "react";
import {EntityType} from "../util/EntityType";
import BannerEditor from "./BannerEditor";
import CategoryEditor from "./CategoryEditor";

interface EntityEditorProps {
    entityId: number|null
    entityType: EntityType,
    changeSelectedId: (id: number|null) => void
}

function EntityEditor(props: EntityEditorProps) {
    return (
        <div className="entity-editor">
            {props.entityType === "banner" ?
                <BannerEditor bannerId={props.entityId} changeSelectedId={props.changeSelectedId}/> :
                <span/>}
            {props.entityType === "category" ?
                <CategoryEditor categoryId={props.entityId} changeSelectedId={props.changeSelectedId}/> :
                <span/>}
        </div>
    );
}

export default EntityEditor;
