import React, {useState} from "react";
import {EntityType} from "../util/EntityType";
import FilterableEntityList from "./FilterableEntityList";
import EntityEditor from "./EntityEditor";

interface EntityManagerProps {
    entityType: EntityType // Entity type to manage
}

function EntityManager(props: EntityManagerProps) {
    // ID of entity managing now
    const [selectedEntityId, setSelectedEntityId] = useState<number|null>(null);
    /*
    State property used for updating a list of entities in FilterableEntityList
    after saving or deleting entity in an editor
    */
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
