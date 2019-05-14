package Workout.Selenium;

import Workout.Logger.LogService;
import Workout.ORM.Model.Operation;
import Workout.ORM.Repository.ProcessStatusRepository;
import com.google.gson.Gson;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.zeroturnaround.zip.commons.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class OperationManager {

    private WebDriver driver;
    private Integer usedDriverId;
    private LogService logger;
    private Operation op;
    private Integer operationTimeout;
    private String screenshootPath;
    private String envProfile;
    private ProcessStatusRepository processRepository;
    private ProcessStatusRepository operationRepository; //TODO <- operation repository. deberioa ser altaRepository etc

    public OperationManager(LogService logger, Operation op, String envProfile, Integer operationTimeout, String screenshootPath,
                            ProcessStatusRepository processRepository) {
        this.logger = logger;
        this.op = op;
        this.operationTimeout = operationTimeout;
        this.screenshootPath = screenshootPath;
        this.processRepository = processRepository;
        this.envProfile = envProfile;
        this.configure();
        this.logger.info("Processing operation " + this.op.getId());
        this.manageOperation();
        this.destroy();
    }

    private void configure() {
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
        FirefoxProfile prof = new FirefoxProfile();


        FirefoxBinary binary = new FirefoxBinary();
        File firefoxProfileFolder = new
                File("src/main/resources/profiles/firefox/default");
        FirefoxProfile profile = new FirefoxProfile(firefoxProfileFolder);
        profile.setAcceptUntrustedCertificates(true);
        opts.setProfile(profile);
        opts.setHeadless(!this.envProfile.equals("dev"));
        this.driver = new FirefoxDriver(opts);
        this.driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }

    protected WebDriver getDriver() {
        return driver;
    }

    public LogService getLogger() {
        return logger;
    }

    public void navigate(String url) {
        this.getDriver().get(url);
        this.waitPageLoad();
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

    private void destroy() {
        if (this.driver != null) {
            this.driver.quit();
        }
    }

    private boolean isSsDown() {
        /* We cannot check this way since this nav has no certificate lol.
        try {
            URL url = new URL(SSUrls.TEST_STATUS_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int code = connection.getResponseCode();
            System.out.println("CODE: " + code);
            return code < 200 || code >= 300;
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }*/
        return false;
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

    protected void takeScreenshoot() {
        try {
            File scrFile = ((TakesScreenshot) this.driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(scrFile, new File(this.screenshootPath + File.separator +
                    this.op.getClass().getSimpleName() + File.separator + this.op.getId() + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkFormErrors() {
        this.logger.info("Comprobando errores de la operación " + this.op.getId());
        /* Primero comprobar errores críticos de la web */
        if (this.isSsDown()) {
            logger.error("La página de la seguridad social está offline (detectado al enviar formulario).");
            return true;
        }
        WebElement errorBox = this.getDriver().findElement(By.id("'DIL'"));

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

        if (!errorBox.isDisplayed() && !isFalseError) {
            this.op.setErrMsg(errorBox.getText());
            this.updateOpStatus("ERROR");
            this.removeOpFromQueue();
            //this.operationRepository.save(this.op);
            this.logger.warning("Error en operación " + this.op.getId() + ": " + errorBox.getText());
            return true;
        }
        return false;
    }

    private void manageOperation() {

        // Do not accept not wanted operations.
        if (this.op.getStatus() != null && !this.op.getStatus().getStatus().equals("AWAITING")) {
            this.logger.info("Descartada operación (por estado) con ID " + this.op.getId() + " del tipo " + this.op.getClass().getSimpleName());
            return;
        }
        if (this.op.getDateInit() != null && this.op.getDateInit().plusSeconds(this.operationTimeout).isBefore(LocalDateTime.now())) {
            this.removeOpFromQueue();
            this.updateOpStatus("TIMED_OUT");
            return;
        }

        /*
        Gracefully process the operation.
         */
        this.updateOpStatus("IN_PROCESS");
        this.op.setDateProcessed(LocalDateTime.now());

        this.logger.info("Procesando operación con ID " + this.op.getId() + " del tipo " + this.op.getClass().getSimpleName());
        if (this.isSsDown()) {
            logger.error("La página de la seguridad social está offline (detectado antes de empezar la operación).");
            return;
        }
        this.initialNavigate();
        boolean success = this.firstForm();

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
        //this.operationRepository.delete(this.op);
    }

    private void executeCallback() {
        String plainUrl = this.op.getCallbackUrl();
        if (plainUrl == null || plainUrl == "") {
            return;
        }
        try {
            Gson gson = new Gson();
            Map<String, Object> params = new HashMap<>();
            params.put("id", this.op.getId());
            //TODO: revisar esto. esto deberia devolver solo Alta, Baja etc. no el nombre compoleto de la clase.
            // REVISAR
            params.put("optype", this.op.getClass().getSimpleName().toLowerCase());
            params.put("result", this.op.getStatus().getStatus());
            params.put("resultmessage", this.op.getErrMsg());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(gson.toJson(params));
            oos.close();

            URL url = new URL(plainUrl + "&response=" + Base64.getEncoder().encodeToString(baos.toByteArray()));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();
        } catch (IOException e) {
            this.logger.info("Ha ocurrido un error al ejecutar el callback de la operación con ID "
                    + this.op.getId() + " del tipo " + this.op.getClass().getSimpleName());
            e.printStackTrace();
        }
    }

    protected void updateOpStatus(String status) {
        this.op.setStatus(this.processRepository.findByStatus(status.toUpperCase()));
        //this.operationRepository.save(this.op);
    }

    abstract void initialNavigate();

    abstract boolean firstForm();
}