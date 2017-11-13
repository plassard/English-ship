package english.pj;

public class ListItem {

    private String name;
    private String number;
    private String score;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }


    public void setScore(String score) {
        this.score = score;
    }

    public String getScore() {
        return score;
    }



    @Override
    public String toString() {
        return name;
    }
}