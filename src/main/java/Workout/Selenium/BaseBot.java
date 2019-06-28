package Workout.Selenium;

import Workout.Logger.LogService;
import Workout.ORM.Model.Operation;
import Workout.ORM.Model.Queue;
import Workout.ORM.QueueService;
import Workout.ORM.Repository.ProcessStatusRepository;
import Workout.ORM.Repository.ProcessTypeRepository;
import Workout.ORM.Repository.QueueRepository;
import com.google.gson.Gson;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.zeroturnaround.zip.commons.FileUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class BaseBot {

    protected Operation op;
    private WebDriver driver;

    protected LogService logger;
    protected boolean isDev;
    protected int operationTimeout;
    protected String screenshootPath;
    protected QueueService queueService;
    protected QueueRepository queueRepository;
    protected ProcessTypeRepository processTypeRepository;
    protected ProcessStatusRepository processStatusRepository;

    public BaseBot(Operation op, HashMap<String, Object> config) {
        this.op = op;
        this.logger = (LogService) config.get("logger");
        this.isDev = config.get("envProfile").equals("dev");
        this.operationTimeout = (Integer) config.get("operationTimeout");
        this.screenshootPath = config.get("screenshootPath").toString();
        this.queueService = (QueueService) config.get("queueService");
        this.queueRepository = (QueueRepository) config.get("queueRepository");
        this.processTypeRepository = (ProcessTypeRepository) config.get("processTypeRepository");
        this.processStatusRepository = (ProcessStatusRepository) config.get("processStatusRepository");
    }

    public boolean configure() {
        this.logger.info("Configure " + this.op.getId());
        /*
        Check if there is a driver available. If not, just wait.
         */
        String os = System.getProperty("os.name").toLowerCase();
        String bits = System.getProperty("sun.arch.data.model");
        String basePath = "src/main/resources/drivers/v0.24/geckodriver.";
        String osExtension = "";
        if (os.contains("mac")) {
            osExtension = "mac";
        } else if (os.contains("windows")) {
            osExtension = "win." + bits + ".exe";
        } else if (os.contains("linux")) {
            osExtension = "linux." + bits;
        }
        System.setProperty("webdriver.gecko.driver", basePath + osExtension);
        FirefoxOptions opts = new FirefoxOptions();
        /*
        Si se desean programar múltiples certificados, se hará aquí.
        Se debe cambiar el perfil al deseado con el certificado instalado.
        Para ello, se cogerá de la instalación original y dicha carpeta de perfil se pondrá en
        resources/profiles/firefox/{{profile}}
        indicando abajo su URL. Para múltiples concurrentes, se debería añadir el campo
        certificate a operation y aquí donde pone default coger this.op.getCertificateName()
        siendo por defecto alguno.
         */
        File firefoxProfileFolder = new
                File("src/main/resources/profiles/firefox/default");
        FirefoxProfile profile = new FirefoxProfile(firefoxProfileFolder);
        profile.setAcceptUntrustedCertificates(true);
        opts.setProfile(profile);
        opts.setHeadless(!this.isDev);
        try {
            this.driver = new FirefoxDriver(opts);
            driver.manage().window().setPosition(new Point(0, 0));
            driver.manage().window().setSize(new Dimension(1024, 768));
            this.driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            this.logger.error("Firefox not installed. Please install it before using bot: Except: " + e.getMessage());
            return false;
        }
        return true;
    }

    public LogService getLogger() {
        return logger;
    }


    protected void destroy() {
        if (this.driver != null) {
            this.endSession();
            //this.driver.quit(); remove due to queue rflection error
        }
        if (this.op != null) {
            this.op.setProcessTime(
                    (int) this.op.getDateProcessed().until(LocalDateTime.now(), ChronoUnit.SECONDS)
            );
            this.queueService.saveOp(op);
        }
        this.op = null;
    }

    protected void manageOperation() {
        this.logger.debug("Managing operation...");

        // Do not accept not wanted operations.
        if (this.op.getDateInit() != null && this.op.getDateInit().plusSeconds(this.operationTimeout).isBefore(LocalDateTime.now())) {
            this.removeOpFromQueue();
            this.updateOpStatus("TIMED_OUT");
            return;
        }
        /*
        Gracefully process the operation.
         */
        this.op.setDateProcessed(LocalDateTime.now());


        this.logger.info("Procesando operación con ID " + this.op.getId() + " del tipo " + this.op.getClass().getSimpleName());
        this.initialNavigate();
        boolean success = false;
        try {
            success = this.firstForm();
        } catch (NoSuchSessionException e) {
            // Nothing since it's not rly an exception (about SS down), already logged.
            return;
        } catch (Exception e) {
            this.logger.error("Ha ocurrido un error al procesar la operación: " + e.getMessage());
            return;
        }

        if (success) {
            this.updateOpStatus("COMPLETED");
        } else {
            this.updateOpStatus("ERROR");
        }
        this.op.setProcessTime((int) (this.op.getDateProcessed().until(LocalDateTime.now(), ChronoUnit.SECONDS)));
        this.executeCallback();
        this.removeOpFromQueue();
    }

    protected void removeOpFromQueue() {
        Queue q = this.queueRepository.findByRefIdAndProcessType(
                this.op.getId(),
                this.processTypeRepository.findByType(this.op.getClass().getSimpleName().toUpperCase())
        );
        if (q != null) {
            this.queueRepository.delete(q);
        }
    }

    private void executeCallback() {
        String plainUrl = this.op.getCallback_url();
        if (plainUrl == null || plainUrl == "") {
            return;
        }
        try {
            Gson gson = new Gson();
            Map<String, Object> params = new HashMap<>();
            params.put("id", this.op.getId());
            params.put("optype", this.op.getClass().getSimpleName().toLowerCase());
            params.put("result", this.op.getStatus().getStatus());
            params.put("resultmessage", this.op.getErrMsg());

            String finalCallback = plainUrl + (plainUrl.indexOf("?") == -1 ? "?" : "&") +
                    "response=" + Base64.getEncoder().encodeToString(gson.toJson(params).getBytes());


            URL con = new URL(finalCallback);
            URLConnection yc = con.openConnection();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            yc.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null)
                System.out.println(inputLine);
            in.close();

            this.logger.info("Ejecutado callback satisfactoriamente:" +
                    +this.op.getId() + " del tipo " + this.op.getClass().getSimpleName() + " -> " + finalCallback);
        } catch (IOException e) {
            this.logger.info("Ha ocurrido un error al ejecutar el callback de la operación con ID "
                    + this.op.getId() + " del tipo " + this.op.getClass().getSimpleName());
        }
    }

    protected void updateOpStatus(String status) {
        this.op.setStatus(this.processStatusRepository.findByStatus(status.toUpperCase()));
        this.queueService.saveOp(this.op);
        this.logger.debug("Update stauts: " + status);
    }


    public WebDriver getDriver() {
        return driver;
    }

    public void navigate(String url) {
        try {
            this.getDriver().get(url);
            this.waitPageLoad();
        } catch (Exception e) {
            logger.error("La página de la seguridad social está offline (detectado antes de empezar la operación).");
            this.endSession();
            this.op.setErrMsg("Seguridad social caída. Petición abortada.");
            this.updateOpStatus("ERROR");
        }
    }


    protected void takeScreenshoot() {
        try {
            File scrFile = ((TakesScreenshot) this.driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(scrFile, new File(this.screenshootPath + File.separator +
                    this.op.getClass().getSimpleName() + File.separator + this.op.getId() + File.separator + System.currentTimeMillis() + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isFalsePositiveError(String webMessageText) {

        HashMap<Integer, String> falsePositives = new HashMap<>();
        falsePositives.put(3408, "Operación realizada correctamente (alta)");
        falsePositives.put(3083, "INTRODUZCA LOS DATOS Y PULSE CONTINUAR");
        falsePositives.put(9125, "ALTA REALIZADA. ASIGNADO CONVENIO DE LA CUENTA");
        falsePositives.put(9086, "ALTA REALIZADA CORRECTAMENTE.CONVENIO COLECTIVO NO ADMITIDO");
        falsePositives.put(3543, "NO EXISTEN DATOS PARA ESTA CONSULTA");
        falsePositives.put(3251, "HAY MAS AFILIADOS A CONSULTAR");
        falsePositives.put(4359, "MOVIMIENTO PREVIO ERRONEO - AFILIADO EN ALTA PREVIA");

        for (Map.Entry<Integer, String> entry : falsePositives.entrySet()) {
            Integer code = entry.getKey();
            if (webMessageText.contains(code.toString() + "*")) {
                return true;
            }
        }
        return false;
    }

    private boolean checkFormErrors() {
        this.logger.info("Comprobando errores de la operación " + this.op.getId());
        /* Primero comprobar errores críticos de la web */
        WebElement errorBox = this.getDriver().findElement(By.id("DIL"));

        /*
         * ¿Hay errores?
         * Puede ser que aparezca la caja para determinar que se hizo correctamente.
         * Para ello, guardamos los códigos satisfactorios y les asociamos una descripción.
         */
        boolean isFalseError = false;
        if (this.isFalsePositiveError(errorBox.getText())) {
            isFalseError = true;
            this.logger.info("Detectado error falsificado. Ignorando error: " + errorBox.getText());
        }

        if (errorBox.isDisplayed() && !isFalseError) {
            this.op.setErrMsg(errorBox.getText());
            this.updateOpStatus("ERROR");
            this.removeOpFromQueue();
            this.queueService.saveOp(this.op);
            this.logger.warning("Error en operación " + this.op.getClass().getSimpleName() + " con ID " + this.op.getId() + ": " + errorBox.getText());
            return true;
        }
        return false;
    }

    public boolean waitFormSubmit(By elemToSearch) {
        this.takeScreenshoot();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.logger.info("Enviando primer formulario...");
        this.getDriver().findElement(elemToSearch).click();
        this.waitPageLoad();
        boolean success = !this.checkFormErrors();
        return success;
    }

    private void waitPageLoad() {
        ExpectedCondition<Boolean> pageLoadCondition = new
                ExpectedCondition<Boolean>() {
                    public Boolean apply(WebDriver driver) {
                        return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
                    }
                };
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(pageLoadCondition);
    }

    private void endSession() {
        this.driver.close();
        this.driver.quit();
        String s;
        Process p;
        try {
            p = Runtime.getRuntime().exec("pkill -9 firefox");
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            while ((s = br.readLine()) != null)
                System.out.println("line: " + s);
            p.waitFor();
            System.out.println ("exit: " + p.exitValue());
            p.destroy();
        } catch (Exception e) {}
    }

    abstract void initialNavigate();

    abstract boolean firstForm();


}