import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JFrame;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;


import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;

public class Window implements ActionListener{

	private JFrame frame;
	private JButton btnChoose,btnFetch;
	private JCheckBox status;
	private Connection	conn ;
	private JLabel show;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Window window = new Window();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws ClassNotFoundException 
	 */
	public Window() throws ClassNotFoundException {
		Connect();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setBackground(Color.ORANGE);
		frame.setBounds(100, 100, 666, 406);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblInsertAndRetrieve = new JLabel("INSERT AND RETRIEVE IMAGE JAVA MYSQL");
		lblInsertAndRetrieve.setBounds(63, 12, 427, 15);
		frame.getContentPane().add(lblInsertAndRetrieve);
		
		show = new JLabel("");
		show.setBounds(108, 105, 141, 96);
		frame.getContentPane().add(show);
		
		btnChoose = new JButton("INSERT IMAGE");
		btnChoose.setForeground(Color.WHITE);
		btnChoose.setBackground(Color.BLUE);
		btnChoose.setBounds(85, 316, 177, 25);
		btnChoose.addActionListener(this);
		frame.getContentPane().add(btnChoose);
		
		status = new JCheckBox("NOT INSERTED");
		status.setBounds(480, 336, 129, 23);
		frame.getContentPane().add(status);
		
		JLabel lblStatus = new JLabel("STATUS");
		lblStatus.setBounds(528, 316, 70, 15);
		frame.getContentPane().add(lblStatus);
		
	    btnFetch = new JButton("RETIREVE IMAGE");
		btnFetch.setForeground(Color.WHITE);
		btnFetch.setBackground(Color.BLUE);
		btnFetch.setBounds(294, 316, 177, 25);
		btnFetch.addActionListener(this);
		frame.getContentPane().add(btnFetch);
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		// TODO Auto-generated method stub
		if(ev.getSource().equals(btnChoose)) {
		
				try {
					//if successful
					if(InsertImage(GetImage())) {
						JOptionPane.showMessageDialog(null, "successful insertion!!");
						status.setSelected(true);
						status.setText("Inserted");
					}else {
						JOptionPane.showMessageDialog(null, "Failed to insert Image","error",JOptionPane.ERROR_MESSAGE);
					}
				} catch (FileNotFoundException | SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		else if(ev.getSource().equals(btnFetch)) 
		{
			RetrieveImage();
		}
		}
		

	//select a file
	private File GetImage() {
		File file = null;
		JFileChooser fc = new JFileChooser();
		
		//filter image type
		FileNameExtensionFilter ef = new FileNameExtensionFilter ("jpg","png");
		fc.addChoosableFileFilter(ef);
		
		//show dialog
		int result = fc.showSaveDialog(null);
		if(result == JFileChooser.APPROVE_OPTION) {
			
		file =  fc.getSelectedFile();
		}
		return file;
	}
	
	
	private boolean InsertImage(File image) throws SQLException, FileNotFoundException {
		PreparedStatement pst = conn.prepareStatement("insert into image values(?,?,?,?)");
		pst.setString(1, null);
		pst.setString(2, image.getName());
		pst.setString(3, image.getAbsolutePath());
		FileInputStream fis = new FileInputStream(image);
		pst.setBlob(4, fis);
		
		int status = pst.executeUpdate();
		if(status>0) {
			return true;
		}else {
			return false;
		}
	}
	
	//connect to the db 
	private void Connect() throws ClassNotFoundException {
	String	driver = "com.mysql.cj.jdbc.Driver";
		String username = "root";
		String password = "";	
			try {
				Class.forName(driver);
			conn = DriverManager.getConnection("jdbc:mysql://localhost/JavaImage",username,password);
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
	}
	
	private void RetrieveImage() {
		
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement("select * from image where id = 2");
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				String name = rs.getString(2);
				Blob blob = rs.getBlob(4);
			     status.setText("Fetch Success!!");
			     status.setSelected(true);
			     
			     //do as Icon
			     int len = (int) blob.length();
			     byte[] bytes = blob.getBytes(1, len);
			     Image img = Toolkit.getDefaultToolkit().createImage(bytes); //create image
			     show.setIcon(new ImageIcon(img));
			     show.setText(name);
			     
			     
			     //write to a file
			     
//			     FileOutputStream out = new FileOutputStream("image.png");
//			     out.write(buf,0,ln);
//			     out.close();
	
			}else {
				JOptionPane.showConfirmDialog(null, "No Image");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			JOptionPane.showConfirmDialog(null, e.getMessage());
		}	
	}
}
