package edu.nsu.chardin.patrol.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "File",
        id = "edu.nsu.chardin.patrol.action.CreateProject"
)
@ActionRegistration(
        displayName = "#CTL_CreateProject"
)
@ActionReference(path = "Menu/File", position = 1300)
@Messages("CTL_CreateProject=Create Project")
public final class CreateProject implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO implement action body
    }
}
