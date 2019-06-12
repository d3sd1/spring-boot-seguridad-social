package Workout.Selenium;

import Workout.Config.SSUrls;
import Workout.ORM.Model.ConsultaIpf;
import Workout.ORM.Model.Operation;
import com.google.gson.Gson;
import org.openqa.selenium.By;

import java.util.HashMap;

public class ConsultaIpfBot extends BaseBot {
    private ConsultaIpf op;

    public ConsultaIpfBot(Operation op, HashMap<String, Object> config) {
        super(op, config);
        this.op = (ConsultaIpf) op;
        this.logger.info("Processing operation " + this.op.getId());
        if (this.configure()) {
            this.manageOperation();
            this.destroy();
        }
    }

    protected void initialNavigate() {
        this.navigate(SSUrls.CONSULTA_IPF);
    }

    protected boolean firstForm() {

        /*
         * **************************************
         * Rellenar primera parte del formulario.
         * **************************************
         */

        /*
         * Rellenar número de afiliación
         * Primer campo, dos dígitos INT
         * Segundo campo, diez dígitos INT
         */
        this.getDriver().findElement(By.name("txt_SDFPROVANT")).sendKeys(this.op.getNaf() != null ? this.op.getNaf().substring(0, 2) : "");
        this.getDriver().findElement(By.name("txt_SDFNUMANT")).sendKeys(this.op.getNaf() != null ? this.op.getNaf().substring(2, 12) : "");

        /*
         * Clickar en el botón de enviar
         * Aquí concluye la primera parte del formulario
         */
        if (this.waitFormSubmit(By.name("btn_Sub2207201008"))) {
            return this.secondForm();
        }
        return false;
    }

    private boolean secondForm() {

        /*
         * Comprobar si hay errores específicos, ya que en esta consulta
         * tienen otra excepción que no tiene nada que ver con los demás.
         */
        String errMsg = this.getDriver().findElement(By.id("SDFNOMBRE")).getText();
        if (errMsg.contains("NUMERO DE AFILIACION INEXISTENTE")) {
            this.updateOpStatus("ERROR");
            this.op.setErrMsg(errMsg);
            this.removeOpFromQueue();
        } else {

            HashMap<String, String> data = new HashMap<>();
            Gson gson = new Gson();
            data.put("ipt", this.getDriver().findElement(By.id("SDFIPFTIPO")).getText());
            data.put("ipf", this.getDriver().findElement(By.id("SDFIPFNUM")).getText());
            data.put("naf", this.getDriver().findElement(By.id("SDFNOMBRE")).getText());
            String json = gson.toJson(data);
            this.op.setData(json);
            this.queueService.saveOp(this.op);
        }

        /*
         * Enviar formulario.
         */
        return this.waitFormSubmit(By.name("btn_Sub2207401004"));
    }
}
