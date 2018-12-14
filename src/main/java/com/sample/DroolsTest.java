package com.sample;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.logger.*;

import com.sample.DroolsTest.RomanticUI;

/**
 * This is a sample class to launch a rule.
 */
public class DroolsTest {

	public static Question active;
	public static KieSession kSession;
	public static KieRuntimeLogger kLogger;
	
	
    public static final void main(String[] args) {
        try {
        	 // KieServices is the factory for all KIE services
	        KieServices ks = KieServices.Factory.get();
	        // From the kie services, a container is created from the classpath
    	    KieContainer kContainer = ks.getKieClasspathContainer();    
        	kSession = kContainer.newKieSession("ksession-rules");     	
        	kLogger = ks.getLoggers().newFileLogger(kSession, "test");
        	
        	init(true);
            kSession.fireAllRules();
            
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    public static void init( boolean exitOnClose) {

        RomanticUI ui = new RomanticUI( );
        ui.createAndShowGUI(exitOnClose);
	}

		public static class RomanticUI extends JPanel implements ActionListener {
			
			private static final long serialVersionUID = 510l;
	        
	        private static JButton buttons[] = new JButton[3];	//Mozliwe odpowiedzi
			private static JLabel l2, l1;							//Tresc pytania

			public RomanticUI() {
				
				
				this.setPreferredSize(new Dimension(800,600));
				this.setBackground(Color.WHITE);
				ImageIcon imageForOne = new ImageIcon("button.jpg");
							
				for(int i = 0; i < 3; i++){						//Ustawiam przyciski
					
					JButton tmp = new JButton(String.valueOf(i), imageForOne);
					tmp.setHorizontalTextPosition(SwingConstants.CENTER);
					tmp.setBounds(20 + 260*i, 450, 250, 100);
					tmp.addActionListener(this);
					buttons[i] = tmp;		//tablica z przyciskami do odpowiedzi
					Border border = new CompoundBorder(
						    LineBorder.createBlackLineBorder(), 
						    LineBorder.createBlackLineBorder());
					tmp.setBorder(border);
					this.add(tmp);
				}
																//Label z pytaniem

				ImageIcon iconLogo = new ImageIcon("rom.png");
				l1 = new JLabel("", SwingConstants.CENTER);
				//l1.setBorder(LineBorder.createGrayLineBorder());
				l1.setBounds(10,10,780,300);
				l1.setIcon(iconLogo);
				
				this.add(l1);
				
				l2 = new JLabel("Pytanie", SwingConstants.CENTER);
				//l2.setBorder(LineBorder.createGrayLineBorder());
				l2.setBounds(10,330,780,100);
				l2.setFont(new Font("Times New Roman", Font.PLAIN, 22));
				
				
				this.add(l2);
	            
			}

			public void createAndShowGUI(boolean exitOnClose) {
				//Create and set up the window.
	            JFrame frame = new JFrame( "Romantic Movie" );
	            frame.setDefaultCloseOperation(exitOnClose ? JFrame.EXIT_ON_CLOSE : JFrame.DISPOSE_ON_CLOSE);

	            setOpaque( true );
	            frame.setContentPane( this );
	            frame.setLayout(null);

	            //Display the window.
	            frame.pack();
	            frame.setLocationRelativeTo(null); // Center in screen
	            
	            frame.setVisible( true );
				
			}
			/**
			 * Metoda ustawiaj¹ca treœci pytania i odpowiedzi na labelu
			 * i przyciskach, po wykryciu przez regu³ê
			 * */
			public static void setGUI(Question n){
				
				String question = n.content;
				String[] possibilities = n.answers;
				active = n;
				
				l2.setText(question);
				
				if(buttons.length - possibilities.length != 0){	//Mniejsza ilosc odp ni¿ 3 (2)
					buttons[2].setVisible(false);	
					setButtons(2);
				}
				
				else{											//Dok³adnie 3 odp
					buttons[2].setVisible(true);
					setButtons(3);
				}
				
				for(int i = 0; i < possibilities.length; i++){
					
					buttons[i].setText(possibilities[i]);	
				}
			}
			
			public static void setGUI_answer(Question n, String s){
				
				for(int i = 0; i < n.answers.length; i++){
					buttons[i].setVisible(false);
				}
				l2.setText(s);
				l2.setLocation(new Point(10,450));
				ImageIcon icon = new ImageIcon(s+".jpg");
				l1.setIcon(icon);
				l1.setLocation(new Point(10,100));
				kLogger.close();
			}
			

			private static void setButtons(int i) {		//Zmiana szerokosci buttona w zaleznosci od ilosci mozliwosci
				int width = 0;
				int fontSize = 10;
				
				if(i == 2){
					width = 375;
					fontSize = 18;
				}
				else if(i == 3){
					width = 250;
					fontSize = 13;
				}
				
				for(int j = 0; j < i; j++){
					buttons[j].setBounds(20 + (width+10)*j, 450, width, 100);
					buttons[j].setFont(new Font("Times New Roman", 1, fontSize));
				}
			}

			public void actionPerformed(ActionEvent e) {
				
				if(e.getSource() == buttons[0]){
					
					active.setPickedAnswer(active.getAnswers()[0]);
					kSession.update(kSession.getFactHandle(active), active);
					kSession.fireAllRules(); 
				}
				else if(e.getSource() == buttons[1]){
					
					active.setPickedAnswer(active.getAnswers()[1]);
					kSession.update(kSession.getFactHandle(active), active);
					kSession.fireAllRules();
				}
				else if(e.getSource() == buttons[2]){
					active.setPickedAnswer(active.getAnswers()[2]);
					kSession.update(kSession.getFactHandle(active), active);
					kSession.fireAllRules();
				}
			}
		}
    
    /**
     * Klasa której obiektem jest pytanie z dan¹ treœci¹ i mo¿liwymi odpowiedziami
     * dodatkowo rozró¿niamy pole picked_answer oznaczaj¹ce wybran¹ odpowiedz przez u¿ytkownika
     **/
    public static class Question{
    	
    	public String content, picked_answer,is_init;
    	public String[] answers;
    	public boolean act;
    	
    	public Question(String q, String[] ans){
    		this.content = q;
    		this.answers = ans;
    		this.picked_answer = null;
    		this.is_init = null;
    		this.act = true;
    	}
    	
    	public String getPickedAnswer() {
			return picked_answer;
		}
    	    	
    	public String getContent(){
    		return content;
    	}

		public String[] getAnswers() {
			return answers;
		}
		
		public void setInit(String b){
			this.is_init = b;
		}
		
		public void setAct(boolean b){
			this.act = b;
		}
		
		public void setPickedAnswer(String answer) {
			this.picked_answer = answer;
		}
    }
}