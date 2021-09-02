import React, {useState} from 'react';
import './App.css';
import NavTabs from './components/NavTabs';
import {EntityType} from './util/EntityType';
import EntityManager from "./components/EntityManager";

function App() {
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
