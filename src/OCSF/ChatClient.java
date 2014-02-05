// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package OCSF;

import OCSF.ocsf.client.*;
import java.io.*;

import quizApp_Classes.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 * 
 * @author Emilienne Pugin
 * @author Rui Chen
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */

public class ChatClient extends AbstractClient
{
	//Instance variables **********************************************

	/**
	 * The interface type variable.  It allows the implementation of 
	 * the display method in the client.
	 */
	QuizApp quizApp; 

	/**
	 * Flags when the client is listening to the thread
	 */
	boolean clientListening = false;

	/**
	 * Stores an ID provided by the client
	 */
	String loginID;//**** Changed for E51a EP

	//Constructors ****************************************************

	/**
	 * Constructs an instance of the chat client.
	 *
	 * @param host The server to connect to.
	 * @param port The port number to connect on.
	 * @param quizApp The interface type variable.
	 */

	public ChatClient(String loginID, String host, int port, QuizApp aQuizApp)
			throws IOException 
			{
		super(host, port); 
		this.quizApp = aQuizApp;
		this.quizApp.setChatClient(this);
		this.loginID = loginID;
		openConnection();
		while(! isClientListening())
		{
			//wait for the client thread to listen
		}
		sendToServer("#login "+loginID);
		System.out.println(loginID + " has logged on.");

		//Ask the server to send the quiz list 
		sendToServer("%SendQuizList");
			}


	//Instance methods ************************************************

	public synchronized boolean isClientListening() {
		// TODO Auto-generated method stub
		return clientListening;
	}
	public synchronized void setClientListening( boolean val)
	{
		clientListening = val;
	}

	protected synchronized void connectionEstablished()
	{
		setClientListening(true);
	}


	/**
	 * This method handles all data that comes in from the server.
	 *
	 * @param msg The message from the server.
	 */
	public void handleMessageFromServer(Object msg) 
	{
		String message = (String) msg;

		//ASSIGNMENT 7:
		if( message.startsWith("#Quiz") )
		{
			//PARSE MESSAGE:
			String [] idName = message.split("<");
			String [] idTemp = idName[1].split(">");
			String id = idTemp[0];
			String [] nameTemp = idName[2].split(">");
			String name = nameTemp[0];

			int totalViewed = 0;
			int totalCorrect = 0;

			quizApp.addQuiz(new Quiz( Integer.parseInt(id), name, totalViewed, totalCorrect, quizApp ) );
		}
		else if( message.startsWith("#Question") )
		{
			//PARSE MESSAGE:
			String [] idName = message.split("<");
			String [] idTemp = idName[1].split(">");
			int quizId = Integer.parseInt( idTemp[0] );
			String [] qIdTemp = idName[2].split(">");
			int questionId = Integer.parseInt(qIdTemp[0]);
			String [] qTxtTemp = idName[3].split(">");
			String qText = qTxtTemp[0];
			String [] qAnsTemp = idName[4].split(">");
			String qAns = qAnsTemp[0];

			//Find the quiz with the matching id
			for(int i = 0; i < quizApp.numberOfQuizzes(); i++)
			{
				if(quizApp.getQuiz(i).getId() == quizId)
				{
					quizApp.getQuiz(i).addQuestion(questionId, qText, qAns);
					break;//have successfully added the question to the quiz and can return
				}
			}
		}
		else if( message.startsWith("#EndOfCommunication") )
		{
			quizApp.setInitialized(true);
		}


		//END ASSIGNMENT 7 ADDITIONS

		quizApp.display(message.toString());

	}

	/**
	 * This method handles all data coming from the UI            
	 *
	 * @param message The message from the UI.    
	 */
	public void handleMessageFromClientUI(String message)
	{
		try
		{
			if( ! message.startsWith("#"))
			{
				sendToServer(message);
			}

			else if( message.startsWith("#quit") )
			{
				System.out.println("Now quitting the program.");
				quit();
			}

			else if( message.startsWith("#logoff") )
			{
				if(!isConnected())
				{
					System.out.println("You are already logged off.");
				}
				else
				{
					closeConnection();
					System.out.println("Connection closed.");
				}
			}

			else if( message.startsWith("#sethost") )
			{
				if(isConnected())
				{
					System.out.println("You are already connected to a server; you must disconnect before setting the host.");
				}
				else
				{
					String newHost = message.substring(9);
					setHost(newHost);
				}
			}

			else if( message.startsWith("#setport") )
			{
				if(isConnected())
				{
					System.out.println("You are already connected to a server; you must disconnect before setting the port.");
				}
				else
				{
					String newPort = message.substring(9);
					try{
						setPort(Integer.parseInt( newPort ));
					}
					catch(NumberFormatException e)
					{
						System.out.println("The port value was invalid.");
					}
				}
			}

			else if( message.startsWith("#login") )
			{
				if(isConnected())
				{
					System.out.println("You are already logged in.");
				}
				else
				{
					openConnection();
					sendToServer("#login "+ message.substring(7));
					System.out.println("You are now logged in.");
				}
			}

			else if( message.startsWith("#gethost") )
			{
				System.out.println("Host is: "+ getHost());
			}

			else if( message.startsWith("#getport") )
			{
				System.out.println("Port is: "+ getPort());
			}

			else//Gives feedback when the user enters # with some text following it, that such a command is not implemented yet if it has not been caught before this 'else'
			{
				System.out.println("This command is invalid.");
			}


		}
		catch(IOException e)
		{
			quizApp.display
			("Could not send message to server.");
			//It does not really make sense to quit if the client made a mistake thinking they were connected and tried to send in a message.
		} 
	}


	/**
	 * This method terminates the client.
	 */
	public void quit()
	{
		try
		{
			closeConnection();
		}
		catch(IOException e) {}
		System.exit(0);
	}

	protected void connectionException(Exception exception)
	{
		System.out.println("WARNING - The server has stopped listening for connections");
		System.out.println("SERVER SHUTTING DOWN! DISCONNECTING!");
		System.out.println("Abnormal termination of connection.");
	}

	//METHODS TO UPDATE THE STORED QUIZZES AND QUESTIONS ON THE SERVER SIDE A7

	/**
	 * Sends a new quiz to the server for storage
	 * The only time this method is to be called is when the quiz has just been created (only has an id and a name, and no questions yet)
	 * @param q
	 * @throws IOException 
	 */
	public void storeNewQuiz(Quiz q) 
	{
		quizApp.addQuiz(q);
		try {
			sendToServer( "%AddQuiz<" + q.getId() +"><"+ q.getName() +">" );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Sends a new question (and the information of which quiz it belongs to) to the server for storage
	 * The only time this method is to be called is when the quiz has already been sent to the server
	 * @param quiz
	 * @param question
	 * @throws IOException
	 */
	public void addQuestion(Quiz quiz, String q, String a) throws IOException
	{
		Question newQ = new Question(q, a, quiz);
		quiz.addQuestion(newQ);
		
		sendToServer( "%AddQuestion<" + quiz.getId() +"><"+ quiz.getName() 
				+"><"+ newQ.getId()  +"><"+ newQ.getQuestionText()  +"><"+ newQ.getAnswerText() +">" );
	}


	public void deleteQuiz(int id, String name) {
		try {
			sendToServer( "%DeleteQuiz<" + id +"><"+ name +">" );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deleteQuestion(int id, String name, int questionId) {
		try {
			sendToServer( "%DeleteQuestion<" + id +"><"+ name +"><"+ questionId +">" );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void removeQuestion(int id, String name, int questionId) {
		try {
			sendToServer( "%QuestionIsLearned<" + id +"><"+ name +"><"+ questionId +">" );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
//End of ChatClient class
