package ipetoolkit.sample

import javafx.beans.property.SimpleStringProperty

import ipetoolkit.workspace.WorkspaceEntry

case class BasicWorkspaceEntry(name : String = "") extends WorkspaceEntry {

  override val view = {
    if(name != ""){
     new BasicWorkspaceEntryView(this, new SimpleStringProperty(name))
    }else{
      new BasicWorkspaceEntryView(this, new SimpleStringProperty(uuid.toString))
    }
  }
}
