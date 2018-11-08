import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

// オブジェクトのシリアライゼーションをためクラス
class Shape implements Serializable{
	int 	shapeType;
	Point 	sPoint;
	Point 	ePoint;
	Point 	point;
	
	Shape(int shapeId, Point startPX, Point startPY) {
		this.shapeType 	= shapeId;
		this.sPoint 	= startPX;
		this.ePoint 	= startPY;
	}
	
	Shape(int shapeType, Point point) {
		this.shapeType 	= shapeType;
		this.point		= point;
	}
	
	int getType() {
		return this.shapeType;
	}
	
	Point getStartP() {
		return this.sPoint;
	}
	
	Point getEndP() {
		return this.ePoint;
	}
	
	Point getPoint() {
		return this.point;
	}
}

class Line extends Shape {
	Line(Point startPX, Point startPY) {
		super(1, startPX, startPY);
	}
}

class Rectangle extends Shape {
	Rectangle(Point point) {
		super(2, point);
	}
}

class Circle extends Shape {
	Circle(Point point) {
		super(3, point);
	}
}

class MyFrame extends JFrame{
	Vector<Shape> shapeDraw = new Vector<Shape>();
	class rightPanel extends JPanel{
		// ボタンのイベント
		rightPanel() {
			this.addMouseListener(new MouseAdapter() {
				// mouse가 click되었을 때(해당 버튼의 도형이 rightPanel에 그려짐 - 사각형,원)
				public void mouseClicked(MouseEvent e) {
					if(btnClicked.getText().equals("사각형그리기")) {
						point 		= e.getPoint();
						Graphics g 	= rightPanel.getGraphics();
						g.drawRect(e.getX()-50, e.getY()-50, 100, 100);
						
						shapeDraw.add(new Rectangle(point));
					} else if(btnClicked.getText().equals("원그리기")) {
						point 		= e.getPoint();
						Graphics g 	= rightPanel.getGraphics();
						g.drawOval(e.getX()-50, e.getY()-50, 100, 100);
						
						shapeDraw.add(new Circle(point));
					} 
				}
				
				// mouse가 press된 후, mouse의 press가 종료되면
				// rightPanel에 선이 그려지게 됨
				public void mousePressed(MouseEvent e) {
					if (btnClicked.getText().equals("선그리기"))
						startP = e.getPoint();
				}
				
				public void mouseReleased(MouseEvent e) {
					if (btnClicked.getText().equals("선그리기")) {
						endP 		= e.getPoint();
						Graphics g 	= rightPanel.getGraphics();
						g.drawLine(startP.x, startP.y, e.getX(), e.getY());
						
						shapeDraw.add(new Line(startP, endP));
					}
				}
			});
		}
		
		// 그림판이 화면에서 사라졌다 다시 화면에 나타날 때, 자동적으로 원래 있던 그림들을 다시 그려주는 메소드
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			Iterator<Shape> it = shapeDraw.iterator();
			
			while(it.hasNext()) {
				Shape nowShape = it.next();
				if (nowShape.getType() == 1){
					Point start 	= nowShape.getStartP();
					Point end 		= nowShape.getEndP();
					
					g.drawLine(start.x, start.y, end.x, end.y);
				} else if (nowShape.getType() == 2) {
					Point point 	= nowShape.getPoint();
					
					g.drawRect(point.x-50, point.y-50, 100, 100);
				} else if (nowShape.getType() == 3) {
					Point point 	= nowShape.getPoint();
					
					g.drawOval(point.x-50, point.y-50, 100, 100);
				}
			
			}
		}
	}
	
	class ObjSeiralToFile{
		String fileName;
		boolean bool;
		
		ObjSeiralToFile(String string, boolean b) {
			// TODO Auto-generated constructor stub
			this.fileName = string;
			this.bool = b;
			
			// bool == true일 경우
			// Save(파일저장)
			if(bool) {
				try {
					FileOutputStream fileOutput 	= new FileOutputStream(fileName);
					ObjectOutputStream objOutput 	= new ObjectOutputStream(fileOutput);
					
					objOutput.writeObject(shapeDraw);
					objOutput.flush();
					objOutput.close();
					fileOutput.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				// bool == false일 경우
				// Load(파일 로드)
				try {
					FileInputStream fileInput 	= new FileInputStream(fileName);
					ObjectInputStream objInput 	= new ObjectInputStream(fileInput);
					
					shapeDraw = (Vector<Shape>) objInput.readObject();
					repaint();
					objInput.close();
					fileInput.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
    }
	
	private JPanel 	leftPanel;
	private JPanel 	rightPanel;
	private JButton drawLine, drawSquare, drawCircle, deleteAll;
	private JButton btnClicked;
	private Color 	btnOriginalColor;
	private Point 	startP;
	private Point 	endP;
	private Point 	point;
	
	MyFrame() {
		// menuBar 생성
		JMenuBar menuBar = new JMenuBar();
		JMenu	 menu    = new JMenu("File");
		menuBar.add(menu);
		
		// 하위 menuBar 생성
		JMenuItem menuItemSave = new JMenuItem("Save");
		menu.add(menuItemSave);
		
		menu.addSeparator();
		
		JMenuItem menuItemLoad = new JMenuItem("Load");
		menu.add(menuItemLoad);
		
		menu.addSeparator();
		
		JMenuItem menuItemExit = new JMenuItem("Exit");
		menu.add(menuItemExit);
		
		// Save(파일 저장)
		menuItemSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				 // TODO Auto-generated method stub
				// 파일 저장 경로 및 확장자 선택
	            JFileChooser fileChooser 	= new JFileChooser();
	            fileChooser.setFileFilter(new FileNameExtensionFilter("myBoard", "areumy"));
	            String fileExtension 		= ((FileNameExtensionFilter)fileChooser.getFileFilter()).getExtensions()[0];
	            
	            // 저장 버튼이 눌릴 경우
	            // 파일 이름을 fileName변수에 GET
	            if (fileChooser.showSaveDialog(menuItemSave) == JFileChooser.APPROVE_OPTION) {
	               String fileName = fileChooser.getSelectedFile().getAbsolutePath();
	               
	             // 객체 직렬화
	             try {
	            	  ObjSeiralToFile objToFile = new ObjSeiralToFile(fileName + "." + fileExtension, true);
	             } catch (Exception argException) {
	                  System.out.println("3: " + argException.getMessage());
	             }
	           }
			}
			
		});
	
		// Load(파일로드)
		menuItemLoad.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// 파일 저장 경로 및 확장자 선택
				JFileChooser fileChooser 	= new JFileChooser();
	            fileChooser.setFileFilter(new FileNameExtensionFilter("myBoard", "areumy"));
	            String fileExtension 		= ((FileNameExtensionFilter)fileChooser.getFileFilter()).getExtensions()[0];
	            
	            // 저장 버튼이 눌릴 경우
	            // 파일 이름을 fileName변수에 GET
	            if (fileChooser.showOpenDialog(menuItemSave) == JFileChooser.APPROVE_OPTION) {
	               String fileName = fileChooser.getSelectedFile().getAbsolutePath();
	             
	             // 객체 직렬화
	             try {
	            	  ObjSeiralToFile objToFile = new ObjSeiralToFile(fileName, false);
	             } catch (Exception argException) {
	                  System.out.println("3: " + argException.getMessage());
	             }
	           }
			}
			
		});
		
		this.setJMenuBar(menuBar);
		
		leftPanel 	= new JPanel();
		rightPanel 	= new rightPanel();
		
		this.getContentPane().setLayout(new GridLayout(0,2));
		
		this.add(leftPanel);
		this.add(rightPanel);
		
		leftPanel.setLayout(new GridLayout(4,0));
		
		drawLine 			= new JButton("선그리기");
		drawSquare 			= new JButton("사각형그리기");
		drawCircle 			= new JButton("원그리기");
	    deleteAll 			= new JButton("모든 도형 지우기");
		btnOriginalColor 	= drawLine.getBackground();
		
		leftPanel.add(drawLine);
		leftPanel.add(drawSquare);
		leftPanel.add(drawCircle);
		leftPanel.add(deleteAll);
		
		// "선그리기"Button이 클릭되었을 때, Button의 배경색을 변경
		drawLine.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getSource() != btnClicked) {
					drawLine.setBackground(Color.YELLOW);
					
					if(btnClicked != null) 
						btnClicked.setBackground(btnOriginalColor);
					
					btnClicked = (JButton)e.getSource();
				}
			}
		});
		
		// "사각형그리기"Button이 클릭되었을 때, Button의 배경색을 변경
		drawSquare.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getSource() != btnClicked) {
					drawSquare.setBackground(Color.YELLOW);
					
					if(btnClicked != null) 
						btnClicked.setBackground(btnOriginalColor);
					
					btnClicked = (JButton) e.getSource();
				}
			}
		});
		
		// "원그리기"Button이 클릭되었을 때, Button의 배경색을 변경
		drawCircle.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getSource() != btnClicked) {
					drawCircle.setBackground(Color.YELLOW);
					
					if(btnClicked != null) 
						btnClicked.setBackground(btnOriginalColor);
					
					btnClicked = (JButton) e.getSource();
				}
			}
		});
		
		// "모든 도형 지우기"Button이 클릭되었을 때, rightPanel 전체 삭제
		deleteAll.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getSource() != btnClicked) {
					deleteAll.setBackground(Color.YELLOW);
					
					if(btnClicked != null) 
						btnClicked.setBackground(btnOriginalColor);
					
					btnClicked = (JButton) e.getSource();
				}
			}
			public void mousePressed(MouseEvent e) {
				/*if (e.getSource() == "모든 도형 지우기")
					System.out.println("ok");
				else 
					System.out.println("no");
				//deleteAll.setBackground(Color.YELLOW);*/			
				if(e.getSource() != btnClicked) {
					deleteAll.setBackground(Color.YELLOW);
					
					if(btnClicked != null) 
						btnClicked.setBackground(btnOriginalColor);
					
					btnClicked = (JButton) e.getSource();
				}
			}
			public void mouserReleased(MouseEvent e) {
				if (e.getSource() == "모든 도형 지우기")
					System.out.println("ok");
				deleteAll.setBackground(Color.WHITE);
			}
		});
		
		setTitle("그림판");
		setSize(700,600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
}

public class paintBoard {
	public static void main(String args[]) {
		MyFrame f = new MyFrame();
	}
}

// textfield
/*class AwtTextFieldControl {
    private Frame mainFrame;
    private Label headerLabel;
    private Label statusLabel;
    private Panel controlPanel, buttonPanel;
 
    public AwtTextFieldControl() {
        prepareGUI();
    }
 
    public static void main(String[] args) {
        AwtTextFieldControl awtControlDemo = new AwtTextFieldControl();
        awtControlDemo.showTextField();
    }
 
    private void prepareGUI() {
        // Frame 에 대한 셋팅
        mainFrame = new Frame("Java AWT 샘플");
        mainFrame.setSize(400, 400);
        mainFrame.setLayout(new GridLayout(4, 1));
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
 
        // 상단에 있는 라벨
        headerLabel = new Label();
        headerLabel.setAlignment(Label.CENTER);
        headerLabel.setText("Control Test : TextField");
 
        // 하단 상태값 라벨
        statusLabel = new Label();
        statusLabel.setText("Status Lable");
        statusLabel.setAlignment(Label.CENTER);
        statusLabel.setSize(350, 100);
 
        controlPanel = new Panel();
        controlPanel.setLayout(new FlowLayout());
 
        buttonPanel = new Panel();
        buttonPanel.setLayout(new FlowLayout());
 
        mainFrame.add(headerLabel);
        mainFrame.add(controlPanel);
        mainFrame.add(buttonPanel);
        mainFrame.add(statusLabel);
        mainFrame.setVisible(true);
    }
 
    private void showTextField() {
        Label namelabel = new Label("아이디 : ", Label.RIGHT);
        Label passwordLabel = new Label("패스워드 : ", Label.CENTER);
        final TextField userText = new TextField(6);
        final TextField passwordText = new TextField(6);
        passwordText.setEchoChar('*');
 
        Button loginButton = new Button("Login");
 
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String data = "아이디 : " + userText.getText();
                data += ", 패스워드 : " + passwordText.getText();
                statusLabel.setText(data);
            }
        });
 
        userText.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                //statusLabel.setText("key Typed");
            }
             
            @Override
            public void keyReleased(KeyEvent e) {
                statusLabel.setText("key release");
            }
             
            @Override
            public void keyPressed(KeyEvent e) {
                statusLabel.setText("key press");
            }
        });
         
        controlPanel.add(namelabel);
        controlPanel.add(userText);
        controlPanel.add(passwordLabel);
        controlPanel.add(passwordText);
        controlPanel.add(loginButton);
        mainFrame.setVisible(true);
    }
}*/