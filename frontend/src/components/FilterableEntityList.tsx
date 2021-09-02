import React, {useEffect, useState} from "react";
import {entityPluralForm, EntityType} from "../util/EntityType";
import SearchBar from "./SearchBar";
import EntityList from "./EntityList";
import {capitalizeFirstLetter} from "../util/Utility";
import {backUrl} from "../backend";
import {Pagination} from "@material-ui/lab";

interface FilterableEntityListProps {
    chooseEntity: (entityId: number|null) => void,
    selectedId: number|null,
    entityType: EntityType,
    listUpdater: boolean
}

const pageSize = 10;

function FilterableEntityList(props: FilterableEntityListProps) {
    const [entityList, setEntityList] = useState<{id: number, name: string}[]>([]);
    const [searchStr, setSearchStr] = useState<string>("");
    const [page, setPage] = useState<number>(1);
    const [pageCount, setPageCount] = useState<number>(1);
    const [error, setError] = useState(null);
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

    return (
        <div className="filterable-entity-list">
            <SearchBar changeHandler={setSearchStr} entityType={props.entityType}/>
            <EntityList listData={entityList} selectListElementHandler={props.chooseEntity} selectedId={props.selectedId}
                        isLoaded={isLoaded} error={error}/>
            <Pagination page={page} defaultPage={1} boundaryCount={2}
                        count={pageCount} onChange={(e, page) => setPage(page)}/>
            <div className="new-entity-button-container">
                <button onClick={() => props.chooseEntity(null)}>
                    Create new {capitalizeFirstLetter(props.entityType)}
                </button>
            </div>
        </div>
    );
}

export default FilterableEntityList;
