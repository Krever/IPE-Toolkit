package ipetoolkit.sample

import javax.xml.bind.annotation.XmlRootElement

import ipetoolkit.workspace.WorkspaceEntry

@XmlRootElement
class BasicWorkspaceEntry extends WorkspaceEntry {

  setName(uuid)

  def this(_name: String) = {
    this()
    setName(_name)
  }

}
