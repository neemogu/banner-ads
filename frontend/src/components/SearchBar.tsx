import React from "react";
import {entityPluralForm, EntityType} from "../util/EntityType";

interface SearchBarProps {
    changeHandler: (searchStr: string) => void, // search bar content callback
    entityType: EntityType, // type of entity to search
    value: string // current search string value
}

function SearchBar(props: SearchBarProps) {
    return (
        <div className="search-bar-container">
            <input type="search" value={props.value} onChange={event => props.changeHandler(event.target.value)}
                   placeholder={"Search " + entityPluralForm.get(props.entityType) + " by name..."}/>
        </div>
    )
}

export default SearchBar;
