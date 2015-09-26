package ipetoolkit.sample

import javafx.beans.property.SimpleStringProperty
import javax.xml.bind.annotation.XmlRootElement

import ipetoolkit.workspace.WorkspaceEntry

@XmlRootElement
class BasicWorkspaceEntry extends WorkspaceEntry {

  var name = ""

  override val view = {
    if (name != "") {
      new BasicWorkspaceEntryView(this, new SimpleStringProperty(name))
    } else {
      new BasicWorkspaceEntryView(this, new SimpleStringProperty(uuid))
    }
  }

  def this(_name: String) = {
    this()
    name = _name
  }
}
