package loroedi.gui.project;

import loroedi.gui.project.model.*;
import loroedi.gui.project.unit.*;

import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.event.*;
import javax.swing.text.JTextComponent;
import javax.swing.BorderFactory;
import javax.swing.border.*;
import java.beans.*;
import java.awt.*;
import java.awt.event.*;

/////////////////////////////////////////////////////////
/**
 * Facilidad para diversos diálogos.
 *
 * @author Carlos Rueda
 * @version 2002-07-24
 */
public class ProjectDialog extends JDialog 
{
	public static Border normalBorder = BorderFactory.createLineBorder(Color.black);
	public static Border redBorder = BorderFactory.createLineBorder(Color.red);
	
    private JOptionPane optionPane;
    private boolean accepted;
    private JButton btnAccept = new JButton("Aceptar");
	
	DocumentListener dl = new DocumentListener()
	{
		public void insertUpdate(DocumentEvent e)
		{
			notifyUpdate();
		}
		public void removeUpdate(DocumentEvent e)
		{
			notifyUpdate();
		}
		public void changedUpdate(DocumentEvent e)
		{
			notifyUpdate();
		}
	};
	ActionListener update_al = new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			notifyUpdate();
		}
	};
	ActionListener accept_al = new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			optionPane.setValue(btnAccept.getText());
		}
	};

	/////////////////////////////////////////////////////////
	private void _setListeners(Object comp)
	{
		if ( comp instanceof Container )
		{
			Container container = (Container) comp;
			Component[] components = container.getComponents();
			for ( int i = 0; i < components.length; i++ )
			{
				_setListeners(components[i]);
			}
		}
		if ( comp instanceof JTextComponent )
		{
			((JTextComponent) comp).getDocument().addDocumentListener(dl);
		}
		if ( comp instanceof JTextField )
		{
			((JTextField) comp).addActionListener(accept_al);
		}
		if ( comp instanceof JComboBox )
		{
			((JComboBox) comp).addActionListener(update_al);
		}
	}

	/////////////////////////////////////////////////////////
    public ProjectDialog(Frame aFrame, String title, Object[] components) 
	{
        super(aFrame, title, true);
        Object[] options = {btnAccept, "Cancelar"};
		accepted = false;
		btnAccept.addActionListener(accept_al);
		
		for ( int i = 0; i < components.length; i++ )
		{
			Object comp = components[i];
			_setListeners(comp);
		}
		
        optionPane = new JOptionPane(components, 
                                    JOptionPane.QUESTION_MESSAGE,
                                    JOptionPane.YES_NO_OPTION,
                                    null,
                                    options,
                                    options[0]);
        setContentPane(optionPane);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() 
		{
			public void windowClosing(WindowEvent we) 
			{
				optionPane.setValue(new Integer(JOptionPane.CLOSED_OPTION));
            }
        });
		
        optionPane.addPropertyChangeListener(new PropertyChangeListener() 
		{
            public void propertyChange(PropertyChangeEvent e) 
			{
                String prop = e.getPropertyName();

                if (isVisible() 
                && (e.getSource() == optionPane)
                && (prop.equals(JOptionPane.VALUE_PROPERTY) ||
                     prop.equals(JOptionPane.INPUT_VALUE_PROPERTY))
				) 
				{
                    Object value = optionPane.getValue();

                    if (value == JOptionPane.UNINITIALIZED_VALUE) {
                        //ignore reset
                        return;
                    }

                    // Reset the JOptionPane's value.
                    // If you don't do this, then if the user
                    // presses the same button next time, no
                    // property change event will be fired.
                    optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
//System.out.println(value.getClass().getName()+ " : " +value);
                    if (value.equals(btnAccept.getText())) 
					{
						if ( dataOk() )
						{
							accepted = true;
							setVisible(false);
						}
                    } 
					else 
					{
                        setVisible(false);
                    }
                }
            }
        });
    }
	
	/////////////////////////////////////////////////////////
	public void setAcceptText(String text)
	{
		btnAccept.setText(text);
	}

	/////////////////////////////////////////////////////////
	public void activate()
	{
		btnAccept.setEnabled(dataOk());
	}

	/////////////////////////////////////////////////////////
    public void notifyUpdate() 
	{
        btnAccept.setEnabled(dataOk());
    }

	/////////////////////////////////////////////////////////
	public boolean dataOk()
	{
		return true;
	}
	
	/////////////////////////////////////////////////////////
    public boolean accepted() 
	{
        return accepted;
    }
}
