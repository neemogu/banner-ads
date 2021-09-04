import React from "react";
import {EntityType} from "../util/EntityType";

interface NavTabsProps {
    selectedTab: EntityType // type of selected tab
    changeTab: (tab: EntityType) => void // change selected tab callback
}

/*
Navigation component used for selecting current active tab
 */

function NavTabs(props: NavTabsProps) {
    return (
        <ul className="navigation">
            <li>
                <button className={props.selectedTab === "banner" ? "active-tab" : "inactive-tab"}
                        onClick={() => props.changeTab("banner")}>
                    {props.selectedTab === "banner" ? "▾" : ""} Banners
                </button>
            </li>
            <li>
                <button className={props.selectedTab === "category" ? "active-tab" : "inactive-tab"}
                    onClick={() => props.changeTab("category")}>
                    {props.selectedTab === "category" ? "▾" : ""} Categories
                </button>
            </li>
        </ul>
    );
}

export default NavTabs;
