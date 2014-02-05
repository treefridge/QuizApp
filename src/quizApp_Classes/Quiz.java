package quizApp_Classes;

/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.18.0.3228 modeling language!*/


import java.util.*;

// line 14 "model.ump"
// line 34 "model.ump"
public class Quiz
{
  @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
  public @interface umplesourcefile{int[] line();String[] file();int[] javaline();int[] length();}

  //------------------------
  // MEMBER VARIABLES
  //------------------------
public static int quizId = 5;
  //Quiz Attributes
  private int id;
  private String name;
  private int totalViewed;
  private int totalCorrect;

  //Quiz Associations
  private List<Question> questions;
  private QuizApp quizApp;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Quiz( String aName, int aTotalViewed, int aTotalCorrect, QuizApp aQuizApp)
  {
    id = ++quizId;
    name = aName;
    totalViewed = aTotalViewed;
    totalCorrect = aTotalCorrect;
    questions = new ArrayList<Question>();
    setQuizApp(aQuizApp);
  }

  //------------------------
  // INTERFACE
  //------------------------

  public Quiz(int aId,  String aName, int aTotalViewed, int aTotalCorrect, QuizApp aQuizApp) {
	    id = aId;
	    name = aName;
	    totalViewed = aTotalViewed;
	    totalCorrect = aTotalCorrect;
	    questions = new ArrayList<Question>();
	    setQuizApp(aQuizApp);
	    
	   
	    if(aId > quizId)
	    {
	    	quizId = aId;
	    }
}

public boolean setId(int aId)
  {
    boolean wasSet = false;
    id = aId;
    wasSet = true;
    return wasSet;
  }

  public boolean setName(String aName)
  {
    boolean wasSet = false;
    name = aName;
    wasSet = true;
    return wasSet;
  }

  public boolean setTotalViewed(int aTotalViewed)
  {
    boolean wasSet = false;
    totalViewed = aTotalViewed;
    wasSet = true;
    return wasSet;
  }

  public boolean setTotalCorrect(int aTotalCorrect)
  {
    boolean wasSet = false;
    totalCorrect = aTotalCorrect;
    wasSet = true;
    return wasSet;
  }

  public int getId()
  {
    return id;
  }

  public String getName()
  {
    return name;
  }

  public int getTotalViewed()
  {
    return totalViewed;
  }

  public int getTotalCorrect()
  {
    return totalCorrect;
  }

  public Question getQuestion(int index)
  {
    Question aQuestion = questions.get(index);
    return aQuestion;
  }

  public List<Question> getQuestions()
  {
    List<Question> newQuestions = Collections.unmodifiableList(questions);
    return newQuestions;
  }

  public int numberOfQuestions()
  {
    int number = questions.size();
    return number;
  }

  public boolean hasQuestions()
  {
    boolean has = questions.size() > 0;
    return has;
  }

  public int indexOfQuestion(Question aQuestion)
  {
    int index = questions.indexOf(aQuestion);
    return index;
  }

  public QuizApp getQuizApp()
  {
    return quizApp;
  }

  public static int minimumNumberOfQuestions()
  {
    return 0;
  }

  public Question addQuestion(int aId, String aQuestionText, String aAnswerText)
  {
    return new Question(aId, aQuestionText, aAnswerText, this);
  }

  public boolean addQuestion(Question aQuestion)
  {
    boolean wasAdded = false;
    if (questions.contains(aQuestion)) { return false; }
    Quiz existingQuiz = aQuestion.getQuiz();
    boolean isNewQuiz = existingQuiz != null && !this.equals(existingQuiz);
    if (isNewQuiz)
    {
      aQuestion.setQuiz(this);
    }
    else
    {
      questions.add(aQuestion);
    }
    wasAdded = true;
    return wasAdded;
  }

  public boolean removeQuestion(Question aQuestion)
  {
    boolean wasRemoved = false;
    //Unable to remove aQuestion, as it must always have a quiz
      questions.remove(aQuestion);
      wasRemoved = true;
    return wasRemoved;
  }

  public boolean addQuestionAt(Question aQuestion, int index)
  {  
    boolean wasAdded = false;
    if(addQuestion(aQuestion))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfQuestions()) { index = numberOfQuestions() - 1; }
      questions.remove(aQuestion);
      questions.add(index, aQuestion);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveQuestionAt(Question aQuestion, int index)
  {
    boolean wasAdded = false;
    if(questions.contains(aQuestion))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfQuestions()) { index = numberOfQuestions() - 1; }
      questions.remove(aQuestion);
      questions.add(index, aQuestion);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addQuestionAt(aQuestion, index);
    }
    return wasAdded;
  }

  public boolean setQuizApp(QuizApp aQuizApp)
  {
    boolean wasSet = false;
    if (aQuizApp == null)
    {
      return wasSet;
    }

    QuizApp existingQuizApp = quizApp;
    quizApp = aQuizApp;
    if (existingQuizApp != null && !existingQuizApp.equals(aQuizApp))
    {
      existingQuizApp.removeQuiz(this);
    }
    quizApp.addQuiz(this);
    wasSet = true;
    return wasSet;
  }

  public void delete()
  {
    for(int i=questions.size(); i > 0; i--)
    {
      Question aQuestion = questions.get(i - 1);
      aQuestion.delete();
    }
    QuizApp placeholderQuizApp = quizApp;
    this.quizApp = null;
    placeholderQuizApp.removeQuiz(this);
  }


  public String toString()
  {
	  String outputString = "";
    return super.toString() + "["+
            "id" + ":" + getId()+ "," +
            "name" + ":" + getName()+ "," +
            "totalViewed" + ":" + getTotalViewed()+ "," +
            "totalCorrect" + ":" + getTotalCorrect()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "quizApp = "+(getQuizApp()!=null?Integer.toHexString(System.identityHashCode(getQuizApp())):"null")
     + outputString;
  }
  
  public int getNumQuestions()
  {
	  return questions.size();
  }

public Question randomQuestion() {
	Question q = null;
	
	if(questions.size() > 0)
	{
		Random random = new Random();
	int rndIndex = random.nextInt(questions.size());
	q = questions.get(rndIndex);
	}
	
	return q;
}

public void incrementTotalViewed() {
	this.totalViewed++;
	
}

public void incrementTotalCorrect()
{
this.totalCorrect++;	
}
}