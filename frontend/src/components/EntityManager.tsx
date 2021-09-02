import React, {useState} from "react";
import {EntityType} from "../util/EntityType";
import FilterableEntityList from "./FilterableEntityList";
import EntityEditor from "./EntityEditor";

interface EntityManagerProps {
    entityType: EntityType
}

function EntityManager(props: EntityManagerProps) {
    const [selectedEntityId, setSelectedEntityId] = useState<number|null>(null);

    return (
        <div className="entity-manager">
            <FilterableEntityList entityType={props.entityType}
                                  selectedId={selectedEntityId}
                                  chooseEntity={setSelectedEntityId}/>
            <EntityEditor entityId={selectedEntityId}
                          entityType={props.entityType}
                          changeSelectedId={setSelectedEntityId}/>
        </div>
    );
}

export default EntityManager;
