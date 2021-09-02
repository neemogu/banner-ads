import React from "react";
import {EntityType} from "../util/EntityType";

interface NavTabsProps {
    selectedTab: EntityType
    changeTab: (tab: EntityType) => void
}

function NavTabs(props: NavTabsProps) {
    return (
        <ul className="navigation">
            <li>
                <button className={props.selectedTab === "banner" ? "active-tab" : "inactive-tab"}
                        onClick={() => props.changeTab("banner")}>
                    Banners
                </button>
            </li>
            <li>
                <button className={props.selectedTab === "category" ? "active-tab" : "inactive-tab"}
                    onClick={() => props.changeTab("category")}>
                    Categories
                </button>
            </li>
        </ul>
    );
}

export default NavTabs;
