package Workout.Selenium;

import Workout.Config.SSUrls;
import Workout.ORM.Model.ConsultaAlta;
import Workout.ORM.Model.Operation;
import com.google.gson.Gson;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.HashMap;
import java.util.List;

public class ConsultaAltaBot extends BaseBot {
    private ConsultaAlta op;

    public ConsultaAltaBot(Operation op, HashMap<String, Object> config) {
        super(op, config);
        this.op = (ConsultaAlta) op;
        this.logger.info("Processing operation " + this.op.getId());
        if (this.configure()) {
            this.manageOperation();
            this.destroy();
        }
    }
    protected void initialNavigate() {
        this.navigate(SSUrls.ALTA);
    }

    protected boolean firstForm() {

        /*
         * Rellenar número de afiliación
         * Primer campo, dos dígitos INT
         * Segundo campo, diez dígitos INT
         */
        this.getDriver().findElement(By.name("txt_SDFTESNAF")).sendKeys(this.op.getNaf() != null ? this.op.getNaf().substring(0, 2) : "");
        this.getDriver().findElement(By.name("txt_SDFNAF")).sendKeys(this.op.getNaf() != null ? this.op.getNaf().substring(2, 10) : "");

        /*
         * Rellenar régimen
         * Un sólo campo de 4 dígitos INT
         */
        this.getDriver().findElement(By.name("txt_SDFREGCTA_NH")).sendKeys(this.op.getCca().getReg());

        /*
         * Rellenar cuenta de cotización
         * Primer campo, dos dígitos INT
         * Segundo campo, nueve dígitos INT
         */
        this.getDriver().findElement(By.name("txt_SDFTESCTA")).sendKeys(this.op.getCca().getCcc().substring(0, 2));
        this.getDriver().findElement(By.name("txt_SDFCUENTA")).sendKeys(this.op.getCca().getCcc().substring(2, 9));

        /*
         * Rellenar fecha real de la consulta.
         * Tres campos (día, mes, año)
         */
        this.getDriver().findElement(By.name("txt_SDFDIA")).sendKeys(String.valueOf(this.op.getFrc().getDayOfMonth()));
        this.getDriver().findElement(By.name("txt_SDFMES")).sendKeys(String.valueOf(this.op.getFrc().getMonthValue()));
        this.getDriver().findElement(By.name("txt_SDFAO")).sendKeys(String.valueOf(this.op.getFrc().getYear()));

        /*
         * Seleccionar del listado como impresión online.
         */
        Select selectBaja = new Select(this.getDriver().findElement(By.id("cbo_ListaTipoImpresion")));
        selectBaja.selectByVisibleText("OnLine");


        if (this.waitFormSubmit(By.name("btn_Sub2207601004"))) {
            return this.secondForm();
        }

        return false;
    }

    private boolean secondForm() {
        if (!this.waitFormSubmit(By.id("Sub0900112079_1_0"))) {
            return false;
        }
        HashMap<String, HashMap<String, Object>> data = new HashMap<>();
        data.put("alta", new HashMap<>());
        data.put("baja", new HashMap<>());

        int fil = 0;
        String elKeyName = "Sub0900112079_{{col}}_{{fil}}";
        while (true) {
            List<WebElement> firstElem = this.getDriver().findElements(By.id(elKeyName.replace("{{col}}", "1").replace("{{fil}}", String.valueOf(fil))));
            if (firstElem.size() == 0) {
                break;
            }
            String elSsName = firstElem.get(0).getText();
            String fecha = this.getDriver().findElement(By.id(elKeyName.replace("{{col}}", "2").replace("{{fil}}", String.valueOf(fil)))).getText();
            if (elSsName.contains("alta")) {
                data.get("alta").put("nombreSS", elSsName);
                data.get("alta").put("fecha", fecha);
            } else if (elSsName.contains("baja")) {
                data.get("baja").put("nombreSS", elSsName);
                data.get("baja").put("fecha", fecha);
            }
            fil++;
        }

        Gson gson = new Gson();
        String json = gson.toJson(data);
        this.op.setData(json);
        this.queueService.saveOp(this.op);

        return true;
    }

}
