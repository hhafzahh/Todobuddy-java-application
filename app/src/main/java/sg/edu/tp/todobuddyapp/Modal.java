package sg.edu.tp.todobuddyapp;

public class Modal {
// 5 strings
    private String task , description,dateline, id , date;


    // generating constructor for the 5 strings-> task,description , dateline , id and date
    public Modal(String task, String description,String dateline, String id, String date) {
        this.task = task;
        this.description = description;
        this.dateline = dateline;
        this.id = id;
        this.date = date;

    }

    public Modal() {
    }

    // generating getter and setter for the variables in this class


    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

