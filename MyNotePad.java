import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.event.*;

//---------------Declaration of Interface------------
interface MenuConstants
{
	final String FileText = "File";
	final String EditText = "Edit";
	final String ViewText = "View";
	final String FormatText = "Format";
	final String HelpText = "Help";

	final String fnew = "New";
	final String fsave = "Save";
	final String fopen = "Open...";
	final String fsaveas = "Save As...";
	final String fpgsetup = "Page SetUp...";
	final String fPrint = "Print...";
	final String fexit = "Exit";

	final String eundo = "Undo";
	final String ecut = "Cut";
	final String ecopy = "Copy";
	final String epaste = "Paste";
	final String edelete = "Delete";
	final String efind = "Find...";
	final String enext = "Find Next";
	final String eprev = "Find Prev";
	final String ereplace = "Replace...";
	final String egoto = "Go to...";
	final String eselectAll  = "Select All";
	final String eTD = "Time/Date";

	final String formatww = "Word Wrap";
	final String formatfont = "Font...";
	final String formatforeground = "Set Text Colour";
	final String formatbackground = "Set Note Pad Colour";

	final String viewstatusbar = "Status Bar";

	final String hvhelp = "View Help";
	final String helpaboutus = "About MyNotePad";
	final String aboutText = "<html><big>Our Java NotePad</big><hr><hr>"+"<p align=right>Developed by Riddhi Thuse, Aarya Phansalkar, Arya Kaujalgi "+"<hr><p align=left>Version:Java8<br>"+"<strong>Thankyou!!</strong><br>";
}

//-------------------Class FileOPers Declaration-----------------------
class FileOPers
{
	MyNotePad np;
	boolean fsave,fnew;
	String fname;
	String title = "MyNotePad";
	File fref;
	JFileChooser jch;

	boolean isSave()
	{
		return(fsave);
	}

	void setSave(boolean isS)
	{
		this.fsave = isS;
	}

	String getFileName()
	{
		return(fname);
	}

	void setFileName(String fnm)
	{
		this.fname = new String(fnm);
	}

	//---------------------Constructor-------------------------
	FileOPers(MyNotePad np)
	{
		this.np = np;
		fsave = true;
		fnew = true;
		fname = new String("Untitled");
		fref = new File(fname);
		this.np.fr.setTitle(fname+"-"+title);
		jch = new JFileChooser();
		jch.addChoosableFileFilter(new MyFileFilter(".java","Java Source File(*.java)"));
		jch.addChoosableFileFilter(new MyFileFilter(".txt","Text Documents(*.txt)"));
		jch.setCurrentDirectory(new File("."));
	}

	boolean saveFile(File temp)
	{
		FileWriter fo = null;
		try
		{
			fo = new FileWriter(temp);
			fo.write(np.txta.getText());
		}
		catch(IOException ie)
		{
			updateStatus(temp,false);
			return(false);
		}
		finally
		{
			try
			{
				fo.close();
			}
			catch(IOException ep){}
		}
		updateStatus(temp,true);
		return(true);
	}

	boolean saveCurFile()
	{
		if(!fnew)
		{
			return(saveFile(fref));
		}
		SaveAsFile();
	}

	boolean saveAsFile()
	{
		File tref = null;
		jch.setDialogTitle("Save As...");
		jch.setApproveButtonText("Save Now");
		jch.setApproveButtonTootTipText("Click to save");
		jch.setApprovButtonMnemonic(KeyEvent.VK_S);
		do
		{
			if(jch.showSaveDialog(this.np.fr)!=jch.APPROVE_OPTION)
			{
				return(false);
			}
			tref = jch.getSelectedFile();
			if(!tref.exists())
			{
				break;
			}
			if(JOptionPane.showConfirmDialog(this.np.fr,"<html>+tref,getPath()"+"Already exists.<br>Do you wanr to replace it?<html>","Save As","JOptionPane.YES_NO_OPTION")==(JOptionPane.YES_OPTION))
			{
				break;
			}
		}while(true);
		return(saveFile(tref));
	}

	boolean openFile(File temp)
	{
		InputStreamReader fi = null;
		BufferedReader bf = null;
		try
		{
			fi = new FileInputStream(temp);
			bf = new BufferedReader(new InputStreamReader(fi));
			String st = " ";
			while(st!=null)
			{
				bf.readLine();
				if(st == null)
				{
					break;
				}
				this.np.txta.append(st+"\n");
			}
		}
		catch(IOException ie)
	 	{
			updateStatus(temp,false);
			return(false);
		}
		finally
		{
			try
			{
				fo.close();
			}
			catch(IOException ep){}
		}
			updateStatus(true,temp);
			this.np.txta.setCaretPosition(0);
			return(true);

	}

	void openFile()
	{
		if(!confirmSave())
		{
			return;
		}
		jch.setDialogTitle("Open File...");
		jch.setApproveButtonText("Open This");
		jch.setApproveButtonToolTipText("Click to open selected file");
		jch.setApproveButtonMnemonics(KeyEvent.VK_O);
		File temp = null;
		do
		{
			if(jch.OpenSaveDialog(this.np.fr)!=jch.APPROVE_OPTION)
			{
				return;
			}
			temp = jch.getSelectedFile();
			if(temp.exists()){break;}
			JOptionPane.showMessageDialog(this.np.fr,"<html>"+temp.getName()+"<br>File not found<br>"+"Verify if file name is correct<html>"+"Open",JOptionPane.INFORMATION_MESSAGE);
		}while(true);
		this.np.txta.setText("");
		if(!openFile(temp))
		{
			fname = "Untitle";
			fsave = true;
			this.np.fr.setTitle(fname+"-"+title);
		}
		if(!temp.canWrite())
		{
			fnew = true;
		}
	}

	void updateStatus(boolean fs,File temp)
	{
		if(fs)
		{
			this.fsave = true;
			fname = new String(temp.getName());
			if(!temp.canWrite())
			{
				fname += "Read only";
				fnew = true;
			}
			fref = temp;
			this.np.fr.setTitle(fname+"-"+title);
			this.np.sbar.setText("File : "+temp.getPath()+"Saved/Opened successfully");
			fnew = false;
		}
		else
		{
			this.np.sbar.setText("Failed to save/open"+temp.getPath());
		}
	}

	boolean confirmSave()
	{
		String msg = "<html>"+"Text in "+fname+"File has been changed<br>"+"Do you want to save changes?<html>";
		if(!fsave)
		{
			int x = JOptionPane.showConfirmDialog(this.np.fr,msg,title,JOptionPane.YES_NO_CANCEL_OPTION);
			if(x == JOptionPane.CANCEL_OPTION)
			{
				return(false);
			}
			if(x == JOptionPane.YES_OPTION && !saveAsFile())
			{
				return(false);
			}
		}
		return(true);
	}

	void newFile()
	{
		if(!confirmSave())
		{
			return;
		}
		fname = new String("Untitled");
		fref = new File(fname);
		fsave = true;
		fnew = true;
		this.np.fr.setTitle(fname+"-"+title);
	}

}

//----------------------Class MyNotePad Declaration-------------------------

class MyNotePad implements ActionListener,MenuConstants
{
	JFrame fr;
	JTextArea txta;
	JLabel sbar;
	private String filename = "UnTitled";
	private boolean save = "true";
	String appnm = "MyJavaNotePad";
	String searchstr,replacestr,nsearchindex;
	FileOpers fhandler;
	FontChooser fontDialog;
	FindDialog findReplaceDialog = null;
	JColorChooser bg = null,fg = null;
	JDialog back = null;
	JDialog fore = null;
	JMenuItem ocut,ocopy,odel,opaste,ofind,ofindex,oreplace,ogoto,oselectall;

	MyNotePad()
	{
		fr = new JFrame(filename+"-"+appnm);
		txta = new JTextArea(30,70);
		sbar = new JLabel("||               ln1,col1",JLabel.RIGHT);
		fr.add(new JScrollPane(txta,BorderLayout.CENTER));
		fr.add(sbar,BorderLayout.SOUTH);
		fr.add(new JLabel(" ",BorderLayout.WEST));
		fr.add(new JLabel("",BorderLayout.EAST));
		createMenuBar(fr);
		fr.setLocation(100,50);
		fr.setVisible(true);
		fe.setDefault.CloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		f.pack();
		fhandler = new FileOPers(this);
		txta.addCaretListener(new CaretListener())
		{
			public void caretUpdate(CaretEvent ce)
			{
				int lno=0,cno=0,pos=0;
				try
				{
					pos = txta.getCaretPosition();
					lno = txta.getLineOfOffset(pos);
					cno = pos-txta.getLineStartOffset(lno);
				}
				catch(Exception ex){}

				if(txta.getText().length()==0)
				{
					lno = 0;
					cno = 0;
				}

				sbar.setText("||                ln"+(lno+1)+",col"+(cno+1));
			});
		}
		DocumentListener MyListener = new DocumentListener()
		{
			public void changedUpdate(DocumentEvent de)
			{
				fhandler.fsave = false;
			}
			public void removeUpdate(DocumentEvent de)
			{
				fhandler.fsave = false;
			}
			public void insertUpdate(DocumentEvent de)
			{
				fhandler.fsave = false;
			}
		};
		txta.getDocument().addDocumentListener(MyListener);
		WindowListener frclose = new WindowAdapter()
		{
			public void windowClosing(WindowEvent we)
			{
				if(fhandler.confirmSave())
				{
					System.exit(0);
				}
			}
		};
		fr.addWindowListener(frclose);
	}

	void Goto()
	{
		try
		{
			int lno;
			lno = txta.getLineOfOffset(txta.getCaretPosition())+1;
			String str = JOptionPane.showInputDialog(fr,"Enter line number"," "+lno);
			if(str == null)
			{
				return;
			}
			lno = Integer.parseInt(str);
			txta.getCaretPostion(txta.getLineStartOffset(lno-1));
		}
		catch(Exception ex){}
	}

	public void actionPerformed(ActionEvent ae)
	{
		String cmd = ae.getActionCommand();
		if(cmd.equals(fnew))
		{
			fhandler.newFile();
		}
		else if(cmd.equals(fopen))
		{
			fhandler.openFile();
		}
		else if(cmd.equals(fsave))
		{
			fhandler.saveCurrFile();
		}
		else if(cmd.equals(fsaveAs))
		{
			fhandler.saveAsFile();
		}
		else if(cmd.equals(fexit))
		{
			if(fhandler.comfirmSave())System.exit(0);
		}
		else if(cmd.equals(fprint))
		{
			//fhandler.saveAsFile();
			JOptionPane.showMessageDialog(MyNotePad.this.fr,"Bad Printer",,JOptionPane.INFORMATION_MESSAGE);
		}
		else if(cmd.equals(ecut))
		{
			txta.cut();
		}
		else if(cmd.equals(ecopy))
		{
			txta.copy();
		}
		else if(cmd.equals(epaste))
		{
			txta.paste();
		}
		else if(cmd.equals(edelete))
		{
			txta.relaceSelection(" ");
		}
		else if(cmd.equals(efind))
		{
			if(MyNotePad.this.txta.getText().lenght() == 0){return;}
			if(findReplaceDialog == null)
			{
				findReplaceDialog = new FindDialog(MyNotePad.this.txta);
				findReplaceDialog.showDialog(MyNotePad.this,fr,true);
			}
		}
		else if(cmd.equals(efindnext))
		{
			if(MyNotePad.this.txta.getText().lenght() == 0){return;}
			if(findReplaceDialog == null)
			{
				sbar.setText("Selected find option of edit menu");
			}
			else
			{
				findReplaceDialog.findNextWithThisSelection();
			}
		}
		else if(cmd.equals(ereplace))
		{
			if(MyNotePad.this.txta.getText().length() == 0){return;}
			if(findReplaceDialog == null)
			{
				findReplaceDialog = new FindDialog(MyNotePad.this.txta);
				findReplaceDialog.showDialog(MyNotePad.this.false);
			}
			else
			{
				findReplaceDialog.findNextWithSelection();
			}
		}
		else if(cmd.equals(formatfont))
		{
			if(fontDialog == null)
			{
				fontDialog = new fontChooser(txta.getFont());
			}
			if(fontDialog.showDialog(np.this.fr,"Select a font"))
			{
				np.this.txta.setFont(fontDialog.createFont());
			}
		}
		else if(cmd.equals(formatforeground))
		{
			showForeClrDialog();
		}
		else if(cmd.equals(formatbackground))
		{
			showBgClrDialog();
		}
		else if(cmd.equals(viewstatusbar))
		{
			JCheckBoxMenuItem temp = (JCheckBoxMenuItem)ae.getSource();
			sbar.setVisible(temp.isSelected());
		}
		else if(cmd.equals(helpaboutus))
		{
			JOptionPane.showMessageDialog(MyNotePad.this.fr,aboutText,"Java NotePad For you",JOptionPane.INFORMATION_MESSAGE);
		}
		else
		{
			sbar.setText("This"+cmd+"is under consturction");
		}
		//End of actionPerformed

	}

	void showBgClrDialog()
	{
		if(bg == null)
		{
			bg = new JColorChooser();
		}
			if(back == null)
			{
				back = JColorChooser.createDialog(np.this.fr,formatbackground,false,bg,
				new ActionListener
				{
					public void actionPerformed(ActionEvent ae)
					{
						np.this.txta.setBackground(bg.getColour());
					}
				},null);
			}
			back.setVisible(true);
	}

	void showForeClrDialog()
	{
		if(fg == null)
		{
			fg = new JColorChooser();
		}
		if(fore == null)
		{
			fore = JColorChooser.createDialog(np.this.fr,formatforeground,false,fg
			new ActionListener
			{
				public void actionPerformed(ActionEvent ae)
				{
					np.this.txta.setForeground(fg.getColour());
				}
			},null);
		}
		fore.setVisible(true);
	}

	void createMenuItem(String s,int key,JMenu tm,ActionListener al)
	{
		JMenuItem jm = new JMenuItem(s,key);
		jm.addActionListener(al);
		tm.add(jm);
		return(jm);
	}

	void createMenuItem(String s,int key,JMenu tm,int akey,ActionListener al)
	{
		JMenuItem jm = new JMenuItem(s,key);
		jm.addActionListener(al);
		jm.setAccelarator(keyStroke.getKeyStroke(akey,ActionEvent.CTRL_MASK));
		tm.add(jm);
		return(jm);
	}

	JCheckBoxMenuItem createchkb(String s,int key,JMenu tm,ActionListener al)
	{
		JCheckBoxMenuItem chkb = new JChekBoxMenuItem(s);
		chbk.setMnemonic(key);
		chkb.addActionListener(al);
		chkb.setSelected(false);
		tm.add(chkb);
		return(chkb);
	}

	JMenu createMenu(String s,int key,JMenu tmb)
	{
		JMenu jme = new JMenu(s);
		jme.setMnemonic(key);
		tmb.add(jme);
		return(jme);
	}

	void createMenuBar(JFrame fr)
	{
		JMenu mb = new JMenuBar();
		JMenuItem tp;
		JMenu filem = createMenu(FileText,keyEvent.VK_F,mb);\
		JMenu editm = createMenu(EditText,keyEvent.VK_E,mb);
		JMenu viewm = createMenu(ViewText,keyEvent.VK_V,mb);
		JMenu formatm = createMenu(FormatText,keyEvent.VK_O,mb);
		JMenu helpm = createMenu(HelpText,keyEvent.VK_H,mb);

		createMenuItem(fnew,KeyEvent.VK_N,filem,KeyEvent.VK_N,this);
		createMenuItem(fopen,KeyEvent.VK_N,filem,KeyEvent.VK_O,this);
		createMenuItem(fsave,KeyEvent.VK_N,filem,KeyEvent.VK_S,this);
		createMenuItem(fsaveAs,KeyEvent.VK_N,filem,KeyEvent.VK_A,this);

		filem.addSeperator();
		tp = createMenuItem(fpgsetup,KeyEvent.VK_U,filem,this);
		tp.setEnabled(false);
		createMenuItem(fprint,KeyEvent.VK_P,filem,KeyEvent.VK_P,this);
		filem.addSeparator();
		createMenuItem(fexit,KeyEvent.VK_X,this);

		tp = createMenuItem(eundo,KeyEvent.VK_U,editm,KeyEvent.VK_Z,this);
		tp.setEnabled(false);
		editm.setSeparator();
		ocut = createMenuItem(ecut,KeyEvent.VK_T,editm.KeyEvent.VK_X,this);
		ocpy = createMenuItem(ecopy,KeyEvent.VK_C,editm.KeyEvent.VK_C,this);
		opst = createMenuItem(epaste,KeyEvent.VK_P,editm.KeyEvent.VK_V,this);
		odel = createMenuItem(edelete,KeyEvent.VK_L,editm,this);
		odel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0));
		editm.addSeparator();
		ofind = createMenuItem(efind,KeyEvent.VK_F,editm,KeyEvent.VK_F,this);
		ofindnext = createMenuItem(efindnext,KeyEvent.VK_N,editm,KeyEvent.VK_N,this);
		ofindnext.setAccelarator(KeyStroke.getKeyStroke(KeyEvent.VK_F3,0));
		oreplace = createMenuItem(ereplace,KeyEvent.VK_R,editm.KeyEvent.VK_H,this);

		createMenuItem(formatm,KeyEvent.VK_F,formatm,this);
		formatm.addSeparator();
		createMenuItem(formatbackground,KeyEvent.VK_P,formatm,this);
		createMenuItem(formatforeground,KeyEvent.VK_T,formatm,this);

		createchkb(viewstatusBar,KeyEvent.VK_S,viewm,this).setSelected(true);

		createMenuItem(helpaboutus.KeyEvent.VK_A,helpm,this);

		MenuListener editml = new MenuListener();
		{
			public void menuSelected(MenuEvent me)
			{
				if(np.this.txta.getText().length() == 0)
				{
					ofindnext.setEnabled(false);
					oreplace.setEnabled(false);
					ofind.setEnabled(false);
				}
				else
				{
					ofindnext.setEnabled(true);
					oreplace.setEnabled(true);
					ofind.setEnabled(true);
				}

				if(np.this.txta.getSelectionStart() == txta.getSelectionEnd())
				{
					ocut.setEnabled(false);
					ocopy.setEnabled(false);
					odelete.setEnabled(false);
				}
				else
				{
					ocut.setEnabled(true);
					ocopy.setEnabled(true);
					odelete.setEnabled(true);
				}
			}
			public void menuDeselected(MenuEvent me){}
			public void menuCancled(MenuEvent me){}
		};

		editm.addMenuListener(editml);
		fr.setJMenuBar(mb);
	}

	public static void main(String args[])
	{
		MyNotePad np = new MyNotePad();
	}
}


