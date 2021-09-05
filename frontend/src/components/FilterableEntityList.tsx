import React, {useEffect, useState} from "react";
import {entityPluralForm, EntityType} from "../util/EntityType";
import SearchBar from "./SearchBar";
import EntityList from "./EntityList";
import {capitalizeFirstLetter} from "../util/Utility";
import {backUrl} from "../backend";
import {Pagination} from "@material-ui/lab";

interface FilterableEntityListProps {
    chooseEntity: (entityId: number|null) => void, // select active entity callback
    selectedId: number|null, // active entity ID
    entityType: EntityType, // entity type
    listUpdater: boolean  // Reload list when this prop is updating
}

const pageSize = 10;

function FilterableEntityList(props: FilterableEntityListProps) {
    const [entityList, setEntityList] = useState<{id: number, name: string}[]>([]);
    const [searchStr, setSearchStr] = useState<string>("");
    // current page
    const [page, setPage] = useState<number>(1);
    // total pages in list
    const [pageCount, setPageCount] = useState<number>(1);
    // is there was an error loading a list or page count
    const [error, setError] = useState(null);
    // is list loaded (or finished trying to load if there was an error)
    const [isLoaded, setIsLoaded] = useState<boolean>(false);

    useEffect(() => {
        setIsLoaded(false);
        setError(null);
        const getUrlParameters = () => {
            return "?page=" + (page - 1) + "&pageSize=" + pageSize + "&searchName=" + searchStr;
        };
        fetch(backUrl + "/" + entityPluralForm.get(props.entityType) + "/pages" + getUrlParameters())
            .then(response => response.text())
            .then(data => setPageCount(Number(data)),
                    error => setError(error))
        fetch(backUrl + "/" + entityPluralForm.get(props.entityType) + "/list" + getUrlParameters())
            .then(response => response.json())
            .then(data => {
                    setEntityList(data.map((e: any) => { return {id: e.id, name: e.name} }));
                    setIsLoaded(true);
                },
                error => setError(error))
    }, [searchStr, page, props.entityType, props.listUpdater]);

    useEffect(() => {
        setSearchStr("");
    }, [props.entityType]);

    useEffect(() => {
        setPage(1);
    }, [searchStr, props.entityType, props.listUpdater]);

    return (
        <div className="filterable-entity-list">
            <div className="filterable-entity-list-header">
                {capitalizeFirstLetter(entityPluralForm.get(props.entityType))}
            </div>
            <SearchBar value={searchStr} changeHandler={setSearchStr} entityType={props.entityType}/>
            <EntityList listData={entityList} selectListElementHandler={props.chooseEntity} selectedId={props.selectedId}
                            isLoaded={isLoaded} error={error}/>
            <div className="entity-list-pagination">
                <Pagination page={page} defaultPage={1} boundaryCount={2}
                            count={pageCount} onChange={(e, page) => setPage(page)}/>
            </div>
            <div className="new-entity-button-container">
                <button onClick={() => props.chooseEntity(null)}>
                    Create new {capitalizeFirstLetter(props.entityType)}
                </button>
            </div>
        </div>
    );
}

export default FilterableEntityList;
