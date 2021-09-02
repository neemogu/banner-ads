import React, {useState} from "react";
import {EntityType} from "../util/EntityType";
import FilterableEntityList from "./FilterableEntityList";
import EntityEditor from "./EntityEditor";

interface EntityManagerProps {
    entityType: EntityType
}

function EntityManager(props: EntityManagerProps) {
    const [selectedEntityId, setSelectedEntityId] = useState<number|null>(null);
    const [listUpdater, setListUpdater] = useState<boolean>(false);
    return (
        <div className="entity-manager">
            <FilterableEntityList entityType={props.entityType}
                                  selectedId={selectedEntityId}
                                  chooseEntity={setSelectedEntityId}
                                  listUpdater={listUpdater}/>
            <EntityEditor entityId={selectedEntityId}
                          entityType={props.entityType}
                          changeSelectedId={setSelectedEntityId}
            listUpdater={setListUpdater}/>
        </div>
    );
}

export default EntityManager;
