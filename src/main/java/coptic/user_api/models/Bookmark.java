//Package where this class belongs
package coptic.user_api.models;

//JPA annotations for database mapping (@Entity, @Table, @Id, @Column, etc.)
import jakarta.persistence.*;

//Entire Class marked as a database entity (table)
//Table name
@Entity
@Table(name = "bookmarks")
public class Bookmark{
    //Id = Primary Key
    //GeneratedValue = primary key generated automatically by the database; auto-increment ID
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    //Name
    @Column(name = "name", nullable=false)
    private String name;

    //UserID
    //Foreign key
    //it's an instantiation of the User entity
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    //Coptic Text
    @Column(name = "coptic_text")
    private String copticText;

    //Transliteration
    @Column(name = "transliteration")
    private String transliteration;

    //English Translation
    @Column(name = "english_translation")
    private String englishTranslation;

    //Arabic Translation
    @Column(name = "arabic_translation")
    private String arabicTranslation;

    //Notes
    @Column(name = "notes")
    private String notes;

    //Default Constructor required by JPA
    //Used when retrieving data
    public Bookmark() {}

    /**
     * Constructor to create a new Bookmark object with provided values
     * @param name Translation's name/title
     * @param user User object (linked to this bookmark)
     * @param copticText Coptic text
     * @param transliteration Transliteration of the Coptic text
     * @param englishTranslation English translation of the Coptic text
     * @param arabicTranslation Arabic translation of the Coptic text
     * @param notes Notes added to the bookmark
     */
     public Bookmark(String name, User user, String copticText, String transliteration, String englishTranslation, String arabicTranslation, String notes){
        this.name = name;
        this.user = user;
        this.copticText = copticText;
        this.transliteration = transliteration;
        this.englishTranslation = englishTranslation;
        this.arabicTranslation = arabicTranslation;
        this.notes = notes;
     }

     //GETTERS & SETTERS
     //Id
     public int getId() {return id;}
     public void setId(int id) {this.id = id;}

     //Name
     public String getName() {return name;}
     public void setName(String name) {this.name = name;}

     //User's ID
     public User getUser() {return user;}
     public void setUser(User user) {this.user = user;}

     //Coptic Text
     public String getCopticText() {return copticText;}
     public void setCopticText(String copticText) {this.copticText = copticText;}

     //Transliteration
     public String getTransliteration() {return transliteration;}
     public void setTransliteration(String transliteration) {this.transliteration = transliteration;}

     //English translation
     public String getEnglishTranslation() {return englishTranslation;}
     public void setEnglishTranslation(String englishTranslation) {this.englishTranslation = englishTranslation;}

     //Arabic translation
     public String getArabicTranslation() {return arabicTranslation;}
     public void setArabicTranslation(String arabicTranslation) {this.arabicTranslation = arabicTranslation;}

     //Notes
     public String getNotes() {return notes;}
     public void setNotes(String notes) {this.notes = notes;}

}