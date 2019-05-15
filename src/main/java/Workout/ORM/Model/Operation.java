package Workout.ORM.Model;

import javax.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
public class Operation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private long id;

    @Column(nullable = true, unique = false)
    private LocalDateTime dateProcessed;

    @Column(nullable = true, unique = false)
    private LocalDateTime dateInit;

    @Column(nullable = true, unique = false)
    private String errMsg;

    @Column(nullable = true, unique = false)
    private int processTime = 0;

    @Column(nullable = true, unique = false)
    private String callback_url = "";

    @OneToOne()
    private ProcessStatus status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getDateProcessed() {
        return dateProcessed;
    }

    public void setDateProcessed(LocalDateTime dateProcessed) {
        this.dateProcessed = dateProcessed;
    }

    public LocalDateTime getDateInit() {
        return dateInit;
    }

    public void setDateInit(LocalDateTime dateInit) {
        this.dateInit = dateInit;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public int getProcessTime() {
        return processTime;
    }

    public void setProcessTime(int processTime) {
        this.processTime = processTime;
    }

    public String getCallback_url() {
        return callback_url;
    }

    public void setCallback_url(String callback_url) {
        this.callback_url = callback_url;
    }

    public ProcessStatus getStatus() {
        return status;
    }

    public void setStatus(ProcessStatus status) {
        this.status = status;
    }
}
