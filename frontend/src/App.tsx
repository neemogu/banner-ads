import React, {useState} from 'react';
import './App.css';
import NavTabs from './components/NavTabs';
import {EntityType} from './util/EntityType';
import EntityManager from "./components/EntityManager";

/*
Main component containing selected tab state
 */

function App() {
  // current selected tab (type of entity)
  const [selectedTab, setSelectedTab] = useState<EntityType>("banner");

  return (
    <div className="App">
      <div className="app-header">
        <NavTabs selectedTab={selectedTab} changeTab={setSelectedTab}/>
      </div>
      <div className="app-body">
        <EntityManager entityType={selectedTab}/>
      </div>
    </div>
  );
}

export default App;
