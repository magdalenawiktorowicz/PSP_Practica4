package es.studium.practicaPSP4;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class clienteFTPBasico extends JFrame
{
	private static final long serialVersionUID = 1L;

	static JTextField txtServidor = new JTextField();
	static JTextField txtUsuario = new JTextField();
	static JTextField txtDirectorioRaiz = new JTextField();
	private static JTextField txtArbolDirectoriosConstruido = new JTextField();
	private static JTextField txtActualizarArbol = new JTextField();
	// Botones
	JButton botonVolver = new JButton("Volver");
	JButton botonRenombrarDir = new JButton("Renombrar carpeta");
	JButton botonRenombrar = new JButton("Renombrar fichero");
	JButton botonCargar = new JButton("Subir fichero");
	JButton botonDescargar = new JButton("Descargar fichero");
	JButton botonBorrar = new JButton("Eliminar fichero");
	JButton botonCreaDir = new JButton("Crear carpeta");
	JButton botonDelDir = new JButton("Eliminar carpeta");
	JButton botonSalir = new JButton("Salir");
	// Lista para los datos del directorio
	static JList<String> listaDirec = new JList<String>();
	// contenedor
	private final Container c = getContentPane();
	// Datos del servidor FTP - Servidor local
	static FTPClient cliente = new FTPClient();// cliente FTP
	String servidor = "127.0.0.1";
	static String user;
	static String pasw;

	static JTextField username = new JTextField();
	static JTextField password = new JPasswordField();
	static Object[] message =
	{ "Username:", username, "Password:", password };
	boolean login;
	static String direcRoot = "/";
	// para saber el directorio y fichero seleccionado
	static String direcSelec;
	static String ficheroSelec = "";
	String itemSelected;
	String currentDirectory = "/";

	public static void main(String[] args) throws IOException
	{
		// dialogo de login
		int option = JOptionPane.showConfirmDialog(null, message, "Login", JOptionPane.OK_CANCEL_OPTION);
		if (option == JOptionPane.OK_OPTION)
		{
			if (username.getText().equals("Magda") && password.getText().equals("Studium2022;"))
			{
				user = username.getText().toString();
				pasw = password.getText().toString();
				// ejecutar el cliente
				new clienteFTPBasico();
			} else
			{
				JOptionPane.showMessageDialog(null, "Credenciales incorrectas", "", JOptionPane.ERROR_MESSAGE);
			}
		}

	} // final del main

	public clienteFTPBasico() throws IOException
	{
		super("CLIENTE B�SICO FTP");
		// para ver los comandos que se originan
		cliente.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
		cliente.connect(servidor); // conexi�n al servidor
		cliente.enterLocalPassiveMode();
		login = cliente.login(user, pasw);
		// Se establece el directorio de trabajo actual
		cliente.changeWorkingDirectory(direcRoot);
		// Obteniendo ficheros y directorios del directorio actual
		FTPFile[] files = cliente.listFiles();
		llenarLista(files, direcRoot);
		// Construyendo la lista de ficheros y directorios
		// del directorio de trabajo actual
		// preparar campos de pantalla
		txtArbolDirectoriosConstruido.setText("<< ARBOL DE DIRECTORIOS CONSTRUIDO >>");
		txtServidor.setText("Servidor FTP: " + servidor);
		txtUsuario.setText("Usuario: " + user);
		txtDirectorioRaiz.setText("DIRECTORIO RAIZ: " + direcRoot);
		// Preparaci�n de la lista
		// se configura el tipo de selecci�n para que solo se pueda
		// seleccionar un elemento de la lista
		listaDirec.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// barra de desplazamiento para la lista
		JScrollPane barraDesplazamiento = new JScrollPane(listaDirec);
		barraDesplazamiento.setPreferredSize(new Dimension(310, 440));
		barraDesplazamiento.setBounds(new Rectangle(5, 30, 310, 440));
		c.add(barraDesplazamiento);
		botonVolver.setBounds(320, 40, 145, 30);
		botonVolver.setEnabled(false);
		c.add(botonVolver);
		botonCreaDir.setBounds(320, 100, 145, 30);
		c.add(botonCreaDir);
		botonRenombrarDir.setBounds(320, 140, 145, 30);
		c.add(botonRenombrarDir);
		botonDelDir.setBounds(320, 180, 145, 30);
		c.add(botonDelDir);

		botonCargar.setBounds(320, 240, 145, 30);
		c.add(botonCargar);
		botonDescargar.setBounds(320, 280, 145, 30);
		c.add(botonDescargar);
		botonRenombrar.setBounds(320, 320, 145, 30);
		c.add(botonRenombrar);
		botonBorrar.setBounds(320, 360, 145, 30);
		c.add(botonBorrar);

		botonSalir.setBounds(320, 420, 145, 30);
		c.add(botonSalir);

		txtServidor.setBounds(40, 480, 180, 25);
		txtServidor.setEditable(false);
		c.add(txtServidor);
		txtUsuario.setBounds(280, 480, 140, 25);
		txtUsuario.setEditable(false);
		c.add(txtUsuario);
		txtDirectorioRaiz.setBounds(40, 510, 380, 25);
		txtDirectorioRaiz.setEditable(false);
		c.add(txtDirectorioRaiz);
		txtArbolDirectoriosConstruido.setBounds(40, 540, 380, 25);
		txtArbolDirectoriosConstruido.setEditable(false);
		c.add(txtArbolDirectoriosConstruido);
		txtActualizarArbol.setBounds(40, 570, 380, 25);
		txtActualizarArbol.setEditable(false);
		c.add(txtActualizarArbol);

		c.setLayout(null);
		// se a�aden el resto de los campos de pantalla
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setSize(490, 650);
		setVisible(true);

		// FUNCIONALIDAD

		botonVolver.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					// volver a la carpeta padre
					cliente.changeToParentDirectory();
					// obtener el directorio actual
					currentDirectory = cliente.printWorkingDirectory();
					// si es el directorio ra�z - deshabilitar el bot�n 'Volver'
					if (currentDirectory.equals("/"))
					{
						botonVolver.setEnabled(false);
					}
					// obtener los ficheros del directorio actual
					FTPFile[] files2 = cliente.listFiles();
					// rellenar la lista de GUI
					llenarLista(files2, currentDirectory);
				} catch (IOException e1)
				{
					e1.printStackTrace();
				}
			}
		});

		listaDirec.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent lse)
			{
				String fic = "";
				if (lse.getValueIsAdjusting())
				{
					itemSelected = listaDirec.getSelectedValue().toString();
					ficheroSelec = "";
					// elemento que se ha seleccionado de la lista
					fic = listaDirec.getSelectedValue().toString();
					if (fic.startsWith("(DIR)"))
					{
						ficheroSelec = fic.substring(6);
					} else
					{
						ficheroSelec = fic;
					}
					txtArbolDirectoriosConstruido.setText("FICHERO SELECCIONADO: " + ficheroSelec);
				}
			}
		});

		listaDirec.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				// comprobar el doble click
				if (e.getClickCount() == 2)
				{
					try
					{
						// comprobar si es un directorio
						if (itemSelected.startsWith("(DIR)"))
						{
							String newDir = itemSelected.substring(6);
							// cambiar el directorio actual a lo que se ha pulsado con doble click
							cliente.changeWorkingDirectory(newDir);
							// actualizar la variable de directorio actual
							currentDirectory = cliente.printWorkingDirectory();
							FTPFile[] files2 = cliente.listFiles();
							llenarLista(files2, currentDirectory);
							// al entrar en el primer directorio, se habilitar� el bot�n 'Volver'
							if (!botonVolver.isEnabled())
							{
								botonVolver.setEnabled(true);
							}
						}

					} catch (IOException e1)
					{
						e1.printStackTrace();
					}
				}
			}
		});

		botonSalir.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					cliente.logout();
					cliente.disconnect();
				} catch (IOException e1)
				{
					e1.printStackTrace();
				}
				System.exit(0);
			}
		});

		botonCreaDir.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String nombreCarpeta = JOptionPane.showInputDialog(null, "Introduce el nombre del directorio",
						"carpeta");
				if (!(nombreCarpeta == null))
				{
					try
					{
						// el m�todo makeDirectory() devuelve true si la operaci�n se realiza con �xito
						if (cliente.makeDirectory(currentDirectory + "/" + nombreCarpeta.trim()))
						{
							String m = nombreCarpeta.trim() + " => Se ha creado correctamente ...";
							JOptionPane.showMessageDialog(null, m);
							txtArbolDirectoriosConstruido.setText(m);
							currentDirectory = cliente.printWorkingDirectory();
							FTPFile[] ff2 = cliente.listFiles();
							llenarLista(ff2, currentDirectory);
						} else
							JOptionPane.showMessageDialog(null, nombreCarpeta.trim() + " => No se ha podido crear ...");
					} catch (IOException e1)
					{
						e1.printStackTrace();
					}
				}
			}
		});

		botonDelDir.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// obtener el elemento seleccionado de la lista
				String nombreCarpeta = itemSelected;
				// control de errores
				// si es un directorio
				if (nombreCarpeta.startsWith("(DIR)"))
				{
					nombreCarpeta = nombreCarpeta.substring(6);
					String directorioAEliminar = currentDirectory + "/" + nombreCarpeta;
					try
					{
						// removeDirectory devuelve true si se ha eliminado el directorio correctamente
						if (cliente.removeDirectory(directorioAEliminar))
						{
							String m = nombreCarpeta.trim() + " => Se ha eliminado correctamente ...";
							JOptionPane.showMessageDialog(null, m);
							txtArbolDirectoriosConstruido.setText(m);
							currentDirectory = cliente.printWorkingDirectory();
							FTPFile[] ff2 = cliente.listFiles();
							llenarLista(ff2, currentDirectory);
						} else
							JOptionPane.showMessageDialog(null,
									nombreCarpeta.trim() + " => No se ha podido eliminar ...");
					} catch (IOException e1)
					{
						e1.printStackTrace();
					}
				}
				// si es un fichero
				else
				{
					// mensaje de error
					JOptionPane.showMessageDialog(null, "El elemento seleccionado es un fichero, no una carpeta.",
							"ERROR", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		botonRenombrarDir.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String nombreCarpeta = itemSelected;
				// control de errores
				// si es un directorio
				if (nombreCarpeta.startsWith("(DIR)"))
				{
					nombreCarpeta = nombreCarpeta.substring(6);
					try
					{
						// preguntar al usuario el nuevo nombre del directorio seleccionado
						String nuevoNombreCarpeta = JOptionPane.showInputDialog("Introduce el nuevo nombre:");
						// si el input del usuario no est� vac�o, ni contiene solo los espacios en
						// blanco
						if ((nuevoNombreCarpeta != null) && (!nuevoNombreCarpeta.isBlank()))
						{
							// rename() devuelve true si la operaci�n se ha ejecutado con �xito
							if (cliente.rename(nombreCarpeta, nuevoNombreCarpeta))
							{
								String m = nombreCarpeta.trim() + " => Se ha renombrado correctamente ...";
								JOptionPane.showMessageDialog(null, m);
								txtArbolDirectoriosConstruido.setText(m);
								currentDirectory = cliente.printWorkingDirectory();
								FTPFile[] ff2 = cliente.listFiles();
								llenarLista(ff2, currentDirectory);
							} else
								JOptionPane.showMessageDialog(null,
										nombreCarpeta.trim() + " => No se ha podido renombrar ...");
						}
					} catch (IOException e1)
					{
						e1.printStackTrace();
					}
				}
				// si es un fichero
				else
				{
					// mensaje de error
					JOptionPane.showMessageDialog(null, "El elemento seleccionado es un fichero, no una carpeta.",
							"ERROR", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		botonRenombrar.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String nombreFichero = itemSelected;
				// control de errores
				// si es un fichero
				if (!nombreFichero.startsWith("(DIR)"))
				{
					String ficheroARenombrar = currentDirectory + "/" + nombreFichero;
					// obtener la extensi�n del fichero
					// utilizar un stream con una expresi�n lambda para separar el nombre y la
					// extensi�n y reducir el resultado a la extensi�n
					String fileExt = Arrays.stream(ficheroARenombrar.split("\\.")).reduce((a, b) -> b).orElse("");
					// System.out.println(fileExt);
					try
					{
						// preguntar al usuario el nuevo nombre del fichero seleccionado
						String nuevoNombreFichero = JOptionPane.showInputDialog("Introduce el nuevo nombre:");
						// si el input del usuario no est� vac�o, ni contiene solo los espacios en
						// blanco
						if ((nuevoNombreFichero != null) && (!nuevoNombreFichero.isBlank()))
						{
							// rename() devuelve true si la operaci�n se ha ejecutado con �xito
							if (cliente.rename(nombreFichero, nuevoNombreFichero + "." + fileExt))
							{
								String m = nombreFichero.trim() + " => Se ha renombrado correctamente ...";
								JOptionPane.showMessageDialog(null, m);
								txtArbolDirectoriosConstruido.setText(m);
								currentDirectory = cliente.printWorkingDirectory();
								FTPFile[] ff2 = cliente.listFiles();
								llenarLista(ff2, currentDirectory);
							} else
								JOptionPane.showMessageDialog(null,
										nombreFichero.trim() + " => No se ha podido renombrar ...");
						}
					} catch (IOException e1)
					{
						e1.printStackTrace();
					}
				}
				// si es un directorio
				else
				{
					// mensaje de error
					JOptionPane.showMessageDialog(null, "El elemento seleccionado es una carpeta, no un fichero.",
							"ERROR", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		botonCargar.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser f;
				File file;
				f = new JFileChooser();
				// solo se pueden seleccionar ficheros
				f.setFileSelectionMode(JFileChooser.FILES_ONLY);
				// t�tulo de la ventana
				f.setDialogTitle("Selecciona el fichero a subir al servidor FTP");
				// se muestra la ventana
				int returnVal = f.showDialog(f, "Cargar");
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					// fichero seleccionado
					file = f.getSelectedFile();
					// nombre completo del fichero
					String archivo = file.getAbsolutePath();
					// solo nombre del fichero
					String nombreArchivo = file.getName();
					try
					{
						SubirFichero(archivo, nombreArchivo);
					} catch (IOException e1)
					{
						e1.printStackTrace();
					}
				}
			}
		});

		botonDescargar.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (itemSelected.startsWith("(DIR)")) {
					JOptionPane.showMessageDialog(null, "El elemento seleccionado es una carpeta, no un fichero.",
							"ERROR", JOptionPane.ERROR_MESSAGE);
				} else {
					String directorio = direcSelec;
					if (!direcSelec.equals("/"))
						directorio = directorio + "/";
					if (!direcSelec.equals(""))
					{
						DescargarFichero(directorio + ficheroSelec, ficheroSelec);
					}
				}
			}
		}); // Fin bot�n descargar
		botonBorrar.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (itemSelected.startsWith("(DIR)"))
				{
					// mensaje de error
					JOptionPane.showMessageDialog(null, "El elemento seleccionado es una carpeta, no un fichero.",
							"ERROR", JOptionPane.ERROR_MESSAGE);
				} else
				{
					String directorio = direcSelec;
					if (!direcSelec.equals("/"))
						directorio = directorio + "/";
					if (!direcSelec.equals(""))
					{
						BorrarFichero(directorio + ficheroSelec, ficheroSelec);
					}
				}

			}
		});
	}

	private static void llenarLista(FTPFile[] files, String direc2)
	{
		if (files == null)
			return;
		// se crea un objeto DefaultListModel
		DefaultListModel<String> modeloLista = new DefaultListModel<String>();
		modeloLista = new DefaultListModel<String>();
		// se definen propiedades para la lista, color y tipo de fuente
		listaDirec.setForeground(Color.blue);
		Font fuente = new Font("Courier", Font.PLAIN, 12);
		listaDirec.setFont(fuente);
		// se eliminan los elementos de la lista
//		listaDirec.removeAll();
		direcSelec = direc2; // directorio actual
		txtActualizarArbol.setText("DIRECTORIO ACTUAL: " + direcSelec);
		// se a�ade el directorio de trabajo al listmodel,
		// primerelementomodeloLista.addElement(direc2);
		// se recorre el array con los ficheros y directorios
		for (int i = 0; i < files.length; i++)
		{
			if (!(files[i].getName()).equals(".") && !(files[i].getName()).equals(".."))
			{
				// nos saltamos los directorios . y ..
				// Se obtiene el nombre del fichero o directorio
				String f = files[i].getName();
				// Si es directorio se a�ade al nombre (DIR)
				if (files[i].isDirectory())
					f = "(DIR) " + f;
				// se a�ade el nombre del fichero o directorio al listmodel
				modeloLista.addElement(f);
			} // fin if
		} // fin for
		try
		{
			// se asigna el listmodel al JList,
			// se muestra en pantalla la lista de ficheros y direc
			listaDirec.setModel(modeloLista);
		} catch (NullPointerException n)
		{
			
		}
	}// Fin llenarLista

	private boolean SubirFichero(String archivo, String soloNombre) throws IOException
	{
		cliente.setFileType(FTP.BINARY_FILE_TYPE);
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(archivo));
		boolean ok = false;
		// directorio de trabajo actual
		cliente.changeWorkingDirectory(direcSelec);
		if (cliente.storeFile(soloNombre, in))
		{
			String s = " " + soloNombre + " => Subido correctamente...";
			txtArbolDirectoriosConstruido.setText(s);
			txtActualizarArbol.setText("Se va a actualizar el �rbol de directorios...");
			JOptionPane.showMessageDialog(null, s);
			FTPFile[] ff2 = null;
			// obtener ficheros del directorio actual
			ff2 = cliente.listFiles();
			// llenar la lista con los ficheros del directorio actual
			llenarLista(ff2, direcSelec);
			ok = true;
		} else
			txtArbolDirectoriosConstruido.setText("No se ha podido subir... " + soloNombre);
		return ok;
	}// final de SubirFichero

	private void DescargarFichero(String NombreCompleto, String nombreFichero)
	{
		File file;
		String archivoyCarpetaDestino = "";
		String carpetaDestino = "";
		JFileChooser f = new JFileChooser();
		// solo se pueden seleccionar directorios
		f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		// t�tulo de la ventana
		f.setDialogTitle("Selecciona el Directorio donde Descargar el Fichero");
		int returnVal = f.showDialog(null, "Descargar");
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			file = f.getSelectedFile();
			// obtener carpeta de destino
			carpetaDestino = (file.getAbsolutePath()).toString();
			// construimos el nombre completo que se crear� en nuestro disco
			archivoyCarpetaDestino = carpetaDestino + File.separator + nombreFichero;
			try
			{
				cliente.setFileType(FTP.BINARY_FILE_TYPE);
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(archivoyCarpetaDestino));
				if (cliente.retrieveFile(NombreCompleto, out))
					JOptionPane.showMessageDialog(null, nombreFichero + " => Se ha descargado correctamente ...");
				else
					JOptionPane.showMessageDialog(null, nombreFichero + " => No se ha podido descargar ...");
				out.close();
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
	} // Final de DescargarFichero

	private void BorrarFichero(String NombreCompleto, String nombreFichero)
	{
		// pide confirmaci�n
		int seleccion = JOptionPane.showConfirmDialog(null, "�Desea eliminar el fichero seleccionado?");
		if (seleccion == JOptionPane.OK_OPTION)
		{
			try
			{
				if (cliente.deleteFile(NombreCompleto))
				{
					String m = nombreFichero + " => Eliminado correctamente... ";
					JOptionPane.showMessageDialog(null, m);
					txtArbolDirectoriosConstruido.setText(m);
					// directorio de trabajo actual
					cliente.changeWorkingDirectory(direcSelec);
					FTPFile[] ff2 = null;
					// obtener ficheros del directorio actual
					ff2 = cliente.listFiles();
					// llenar la lista con los ficheros del directorio actual
					llenarLista(ff2, direcSelec);
				} else
					JOptionPane.showMessageDialog(null, nombreFichero + " => No se ha podido eliminar ...");
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
	}// Final de BorrarFichero
}// Final de la clase ClienteFTPBasico