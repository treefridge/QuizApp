package com.emilienne.selfquizapp;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import quizApp_Classes.Question;
import quizApp_Classes.Quiz;
import quizApp_Classes.QuizApp;
import OCSF.ChatClient;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity {

	/**
	 * Association
	 */
	private QuizApp app;

	/**
	 * Default for user login name, also dictates the folder name under which the quizzes are stored on the server end
	 */
	private static final String DEFAULT_LOGIN = "Android_user1";
	private String clientId = DEFAULT_LOGIN;

	/**
	 * Android UI Components. Used to create and maintain the quiz list dropdown (spinner) 
	 * and the associated Random Question button
	 */
	private Spinner quizSpinner;
	private Button spinnerGoButton;

	/**
	 * Android UI Component. Used to create and maintain the Random Question dialog (popup)
	 */
	private Question currentQuestion;

	/**
	 * Android UI Components. Used to store the text entered in the New Question dialog 
	 * in the currentQuiz chosen in the Spinner (dropdown)
	 */
	EditText answerText;
	EditText questionText;
	Quiz currentQuiz;

	@Override
	/**
	 * Method called upon the launch of the App (Android's version of a Main method)
	 */
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		String clientID = DEFAULT_LOGIN;
		app = new QuizApp(clientID);

		//GET STORED QUIZZES AND QUESTIONS
		File quizDir = new File(clientId);//Quizzes stored in a directory of the same name as the client's id
		File [] quizFiles = quizDir.listFiles();//get a list of the quiz folder files in the client's directory

		if(quizFiles == null)
		{
			//Create new user folder
			File userDir= new File(clientId);
			userDir.mkdir();			
		}
		else
		{
			//FOR EACH QUIZ folder file (each folder file name = 'id.name' of quiz):
			for(int i = 0; i < quizFiles.length; i++)//all the quiz directories for the user
			{
				String [] quizIdName = quizFiles[i].getName().split("\\.");
				String quizId = quizIdName[0];
				String quizName = quizIdName[1];

				//Save quiz id and name
				app.addQuiz(Integer.parseInt(quizId), quizName, 0 , 0);

				File questionDir = new File(clientId+"/"+quizFiles[i].getName());//Questions stored in the folder of the quiz at quizFiles[i]

				if(!questionDir.exists())
				{
					break; 
				}

				File [] questionFiles = questionDir.listFiles();//get a list of the question txt files, in the quiz folder 'i', in the client's directory

				//FOR EACH QUESTION text file
				for(int i2 = 0; i2 < questionFiles.length; i2++)
				{
					try{
						BufferedReader br = new BufferedReader( new InputStreamReader(new FileInputStream(clientId+"/"+quizFiles[i].getName()+"/"+questionFiles[i2].getName())));//NEED TO HAVE THE DIR/?
						String line;
						ArrayList<String> fileLines = new ArrayList<String>();
						while ((line = br.readLine()) != null) //read text file line by line
						{
							fileLines.add(line);
						}

						//Check whether the first line of the text file 'isLearned', if not, add the question to the Quiz corresponsing to 'i' within QuizApp app.
						if(fileLines.get(0).equals("false"))//isLearned
						{
							String [] questionId = questionFiles[i2].getName().split("\\.txt");//txt file name is 'questionid.txt' so the id is extracted without the '.txt'
							//Add quiz at the end of the array of QuizApp:
							app.getQuiz(app.numberOfQuizzes()-1).addQuestion(Integer.parseInt(questionId[0]), fileLines.get(1), fileLines.get(2));
						}
						br.close();
					}catch(IOException e)
					{
						System.out.println("Error reading from file.");
					}

				}

			}
		}

		//SPINNER:
		addItemsOnQuizSpinner();//Calls method to populate the dropdown
		addListenerOnGoQuizButton();//Readies the Random Question button 
	}

	/**
	 * Add the items into the spinner (dropdown menu) that lists the quizzes dynamically
	 */
	private void addItemsOnQuizSpinner() {

		quizSpinner = (Spinner) findViewById(R.id.quizSpinner);
		List<String> list = new ArrayList<String>();

		if(app.numberOfQuizzes() == 0)//If there are no stored quizzes then there are no quizzes to display. Must inform the user.
		{
			list.add("No Quizzes Found. Use the button above.");
		}

		for(int i = 0; i < app.numberOfQuizzes(); i++)//Every quiz
		{
			list.add(app.getQuiz(i).getName());//add to the spinner's list
		}

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		quizSpinner.setAdapter(dataAdapter);
		quizSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			/**
			 * Method that gets called when an item in the spinner is selected
			 */
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				String selectedQuiz = String.valueOf(quizSpinner.getSelectedItem());//Get the string corresponding to the selected item in the spinner

				currentQuiz = null;

				int quizIndex = 0;
				for(; quizIndex < app.numberOfQuizzes(); quizIndex++)//Every quiz in QuizApp
				{
					//Check whether the spinner's selected quiz string matches the name of one of the quizzes stored in QuizApp:
					if(app.getQuiz(quizIndex).getName().compareTo(selectedQuiz) == 0)
					{
						currentQuiz = app.getQuiz(quizIndex); //currentQuiz now equals the quiz that is chosen in the spinner
						break;
					}
				}		
				if(currentQuiz != null)//If there is an item in the spinner selected (and this occurs only when there is >0 quizzes in QuizApp)
				{
					//Set the total number of questions statistics at the bottom of the display:
					TextView totalQuestions = (TextView) findViewById(R.id.totalQs);
					totalQuestions.setText( Integer.toString( currentQuiz.numberOfQuestions() ) );
					//Set the grade for the quiz at the bottom of the display: (this data is non-persistent between app shut downs)
					TextView quizGrade = (TextView) findViewById(R.id.quizGrade);
					if(currentQuiz.getTotalViewed() != 0)
					{
						quizGrade.setText(Integer.toString(  (int) ((double) currentQuiz.getTotalCorrect() / (double) currentQuiz.getTotalViewed()) * 100 )+ " %");
					}
					else
					{
						quizGrade.setText("0 %");
					}
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				//nothing
			}

		});
	}

	/**
	 * Listener for the "Random Question" button. Will launch a dialog that displays the question, 
	 * which launches another dialog that displays the answer. The user exits the second dialog by indicating whether they got the answer/wish to be asked again later.
	 *  The server gets informed if the user does not wish to see the answer again and alters the Question text file accordingly. The statistics get updated.
	 */
	private void addListenerOnGoQuizButton() {
		quizSpinner = (Spinner) findViewById(R.id.quizSpinner);
		spinnerGoButton = (Button) findViewById(R.id.goQuizSpinnerButton);

		spinnerGoButton.setOnClickListener(new OnClickListener() {

			/**
			 * When the user presses the GoQuiz button a popup with a random question must be displayed
			 */
			@Override
			public void onClick(View v) {

				//Open Dialog!
				String selectedQuiz = String.valueOf(quizSpinner.getSelectedItem());
				currentQuestion = null;
				Quiz currentQuiz = null;
				int quizIndex = 0;
				for(; quizIndex < app.numberOfQuizzes(); quizIndex++)//Every quiz
				{
					if(app.getQuiz(quizIndex).getName().compareTo(selectedQuiz) == 0)
					{
						currentQuiz = app.getQuiz(quizIndex);
						break;
					}
				}		

				if(quizIndex < app.numberOfQuizzes())
				{
					currentQuestion = app.getQuiz(quizIndex).randomQuestion();
				}

				if(currentQuiz != null && currentQuestion != null)
				{
					//ALERT DIALOG FOR DISPLAYING A RANDOM QUESTION (POPUP)
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					builder.setTitle("Think of the answer to: ");

					// Set up the displayed question
					builder.setMessage(currentQuestion.getQuestionText());

					// Set up the buttons
					builder.setPositiveButton("See Answer", new DialogInterface.OnClickListener() { 

						/**
						 * Executes when the user has viewed the Random Question and wishes to see the Answer.
						 * Opens a new Popup displaying the answer and choices for the user regarding their ability to answer the question.
						 */
						@Override
						public void onClick(DialogInterface dialog, int which) {

							//ALERT DIALOG FOR DISPLAYING THE ANSWER TO THE RANDOM QUESTION
							AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
							builder.setTitle("Did you get this answer ? ");

							// Set up the displayed question
							builder.setMessage(currentQuestion.getAnswerText());

							// Set up the buttons
							builder.setPositiveButton("No", new DialogInterface.OnClickListener() { 
								/**
								 * Executes when the user did not answer the question correctly.
								 * The question will be available to be randomly chosen again in the future. The total number of viewed questions for the quiz increases (Grade lowers)
								 */
								@Override
								public void onClick(DialogInterface dialog, int which) {
									currentQuestion.getQuiz().incrementTotalViewed();//Increment total number of questions viewed for the quiz

									//Update the stat displays at the bottom of the activity:
									TextView totalQuestions = (TextView) findViewById(R.id.totalQs);
									totalQuestions.setText( Integer.toString( currentQuestion.getQuiz().numberOfQuestions() ) );
									TextView quizGrade = (TextView) findViewById(R.id.quizGrade);
									if(currentQuestion.getQuiz().getTotalViewed() != 0)
									{
										quizGrade.setText(Integer.toString((int)( (double) (currentQuestion.getQuiz().getTotalCorrect()) / (double) (currentQuestion.getQuiz().getTotalViewed()) * 100 ) )+ " %");
									}
									else
									{
										quizGrade.setText("0 %");
									}

									dialog.cancel();
								}
							});
							builder.setNegativeButton("Yes & ask it later", new DialogInterface.OnClickListener() {

								/**
								 * Executes when the user answered the question correctly and wishes to be asked the question again in the future.
								 * Increments the total questions viewed and the total questions correctly answered for the quiz. Also updates the display of statistics
								 */
								@Override
								public void onClick(DialogInterface dialog, int which) {
									currentQuestion.getQuiz().incrementTotalViewed();
									currentQuestion.getQuiz().incrementTotalCorrect();

									//Update the stat displays:
									TextView totalQuestions = (TextView) findViewById(R.id.totalQs);
									totalQuestions.setText( Integer.toString( currentQuestion.getQuiz().numberOfQuestions() ) );
									TextView quizGrade = (TextView) findViewById(R.id.quizGrade);
									if(currentQuestion.getQuiz().getTotalViewed() != 0)
									{
										quizGrade.setText(Integer.toString((int)( (double) (currentQuestion.getQuiz().getTotalCorrect()) / (double) (currentQuestion.getQuiz().getTotalViewed()) * 100 ) )+ " %");
									}
									else
									{
										quizGrade.setText("0 %");
									}

									dialog.cancel();//close the popup
								}
							});
							builder.setNeutralButton("Yes & remove it", new DialogInterface.OnClickListener() {

								/**
								 * Executes when the user answered the question correctly and wishes NOT to be asked the question again in the future.
								 * Increments the total questions viewed and the total questions correctly answered for the quiz. Also updates the display of statistics
								 * The question txt file gets updated so that isLearned is true and the question is removed from the QuizApp quiz selected
								 */
								@Override
								public void onClick(DialogInterface dialog, int which) {
									Quiz temp = currentQuestion.getQuiz();
									currentQuestion.getQuiz().incrementTotalViewed();
									currentQuestion.getQuiz().incrementTotalCorrect();
									dialog.cancel();

									//Update the stat displays:
									TextView totalQuestions = (TextView) findViewById(R.id.totalQs);
									totalQuestions.setText( Integer.toString( currentQuestion.getQuiz().numberOfQuestions() -1 ) );
									TextView quizGrade = (TextView) findViewById(R.id.quizGrade);
									if(currentQuestion.getQuiz().getTotalViewed() != 0)
									{
										quizGrade.setText(Integer.toString((int)( (double) (currentQuestion.getQuiz().getTotalCorrect()) / (double) (currentQuestion.getQuiz().getTotalViewed()) * 100 ) )+ " %");
									}
									else
									{
										quizGrade.setText("0 %");
									}

									//Remove the question:
									File question = new File(clientId + "/" +currentQuestion.getQuiz().getId()+ "." +currentQuestion.getQuiz().getName() +"/"+currentQuestion.getId()+ ".txt");

									//get the current question data:
									BufferedReader br;
									try {
										br = new BufferedReader( new InputStreamReader(new FileInputStream(clientId+"/"+currentQuestion.getId()+ "." +currentQuestion.getQuiz().getName()+"/"+ currentQuestion.getId() + ".txt")));


										String line;

										ArrayList<String> fileLines = new ArrayList<String>();

										while ((line = br.readLine()) != null) //read text file line by line
										{
											fileLines.add(line);
										}

										//replace the question file to have isLearned = true
										PrintWriter writer = new PrintWriter(new FileWriter(question, false));//want to overwrite the current question file
										writer.println("true");
										writer.println(fileLines.get(1));
										writer.println(fileLines.get(2));
										writer.close();
									} catch (FileNotFoundException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									catch(Exception e)
									{
										// TODO Auto-generated catch block
										e.printStackTrace();										
									}

									//Delete question from current instances:
									currentQuestion.getQuiz().removeQuestion(currentQuestion);
									currentQuestion.delete();
								}
							});

							builder.show();
							dialog.cancel();
						}
					});
					builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						/**
						 * Allows the user to cancel from the question answering once they have viewed the question text, but before viewing the answer popup text. Nothing gets altered, the popup is simply dismissed.
						 */
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});

					builder.show();
				}

			}
		});
		currentQuestion = null;
	}

	/**
	 * Provided by default
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	/** 
	 * Called when the user clicks the New Question button. Opens a dialog where the user enters the question & answer text and updates the data stored by the server as well as the QuizApp quiz list.
	 */
	public void newQuestion(View view) {

		String selectedQuiz = String.valueOf(quizSpinner.getSelectedItem());

		currentQuiz = null;

		int quizIndex = 0;
		for(; quizIndex < app.numberOfQuizzes(); quizIndex++)//Every quiz
		{
			if(app.getQuiz(quizIndex).getName().compareTo(selectedQuiz) == 0)
			{
				currentQuiz = app.getQuiz(quizIndex);
				break;
			}
		}		
		if(currentQuiz != null)
		{
			//ALERT DIALOG FOR DISPLAYING A RANDOM QUESTION
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			builder.setTitle("Enter new question: ");

			//Set up the text input spaces:
			questionText = new EditText(MainActivity.this);
			answerText = new EditText(MainActivity.this);

			questionText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
			answerText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
			questionText.setHint("Question text");
			answerText.setHint("Answer text");

			LinearLayout ll=new LinearLayout(this);
			ll.setOrientation(LinearLayout.VERTICAL);
			ll.addView(questionText);
			ll.addView(answerText);
			builder.setView(ll);


			// Set up the buttons
			builder.setPositiveButton("Store", new DialogInterface.OnClickListener() { 

				/**
				 * 
				 */
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String qtext = questionText.getText().toString();
					String atext = answerText.getText().toString();

					if( (! qtext.equals("")) && (! atext.equals("")) && (atext.trim().length() > 0) && (qtext.trim().length() > 0))
					{
						Question newQ = new Question(qtext, atext, currentQuiz);
						currentQuiz.addQuestion(newQ);

						//Store to file:
						File question = new File(clientId + "/" +currentQuiz.getId()+ "." +currentQuiz.getName() +"/"+newQ.getId()+ ".txt");
						PrintWriter writer;

						try {
							writer = new PrintWriter(new FileWriter(question, false));
							writer.println("false");
							writer.println(newQ.getQuestionText());
							writer.println(newQ.getAnswerText());
							writer.close();
						} catch (IOException e) {
							e.printStackTrace();
						}

						//Update the stat displays:
						TextView totalQuestions = (TextView) findViewById(R.id.totalQs);
						totalQuestions.setText( Integer.toString( currentQuiz.numberOfQuestions() ) );
						TextView quizGrade = (TextView) findViewById(R.id.quizGrade);
						if(currentQuiz.getTotalViewed() != 0)
						{
							quizGrade.setText(Integer.toString(  (int)( (double) (currentQuiz.getTotalCorrect()) / (double) (currentQuiz.getTotalViewed())) * 100 ) + " %");
						}
						else
						{
							quizGrade.setText("0 %");
						}
					}
				}

			});
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				
				/**
				 * User changed mind about creating a new question
				 */
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});

			builder.show();
		}
	}


	/**
	 *  Called when the user clicks the New Quiz button. Launches a dialog allowing the user to enter a name for the new quiz. The server gets the information about the new quiz.
	 */
	public void newQuiz(View view) {

		//ALERT DIALOG FOR NEW QUIZ
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("New Quiz");

		// Set up the input
		final EditText input = new EditText(this);

		// Specify the type of input expected
		input.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
		builder.setView(input);
		input.setHint("Enter new quiz name");

		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
			
			/**
			 * User, if they have entered a valid quiz name, should have the new quiz stored
			 */
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String m_Text = input.getText().toString();
				if(! m_Text.equals("") && (m_Text.trim().length() > 0))
				{
					Quiz q = new Quiz(m_Text, 0, 0, app);
					app.addQuiz(q);
					addItemsOnQuizSpinner();
					File newQuiz = new File(clientId + "/" + q.getId() + "." + q.getName());
					newQuiz.mkdir();
				}
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			/**
			 * When the user changes their mind about creating a new quiz and wants to return to main app menu
			 */
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.show();
	}



	/** 
	 * Called when the user clicks the Delete Quiz button. The spinner (dropdown list) must have a quiz selected, and that quiz gets removed. The server gets updated.
	 */
	public void deleteQuiz(View view) {
		String selectedQuiz = String.valueOf(quizSpinner.getSelectedItem());

		for(int i = 0; i < app.numberOfQuizzes(); i++)//Every quiz
		{
			if(app.getQuiz(i).getName().compareTo(selectedQuiz) == 0)
			{
				Quiz q = app.getQuiz(i);
				File quizFolder = new File(clientId + "/" +q.getId()+ "." +q.getName());
				File[] files = quizFolder.listFiles();

				//Delete all the question files:
				if(files != null) { //some JVMs return null for empty dirs
					for(File f: files) {
						f.delete();
					}
				}

				//Delete the folder:
				quizFolder.delete();
				q.delete();
				app.removeQuiz(q);
				break;
			}
		}		
		addItemsOnQuizSpinner();
		TextView totalQuestions = (TextView) findViewById(R.id.totalQs);
		totalQuestions.setText("");
		TextView quizGrade = (TextView) findViewById(R.id.quizGrade);
		quizGrade.setText("");
	}

}
