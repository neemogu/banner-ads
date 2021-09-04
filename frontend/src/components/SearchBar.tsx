import React from "react";
import {entityPluralForm, EntityType} from "../util/EntityType";

interface SearchBarProps {
    changeHandler: (searchStr: string) => void, // search bar content callback
    entityType: EntityType // type of entity to search
}

function SearchBar(props: SearchBarProps) {
    return (
        <div className="search-bar-container">
            <input type="search" onChange={event => props.changeHandler(event.target.value)}
                   placeholder={"Search " + entityPluralForm.get(props.entityType) + " by name..."}/>
        </div>
    )
}

export default SearchBar;
