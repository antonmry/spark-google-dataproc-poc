import geb.Browser
import org.codehaus.groovy.runtime.InvokerHelper

class DatabricksGeb extends Script {

    def run() {
        System.setProperty("webdriver.gecko.driver", "geckodriver");

        def organization = login(PrivateData.email, PrivateData.password)
        String clusterName = UUID.randomUUID().toString()
        clusterName = "89c87aad-cb23-48d3-9060-9e0d5354ed2a"
        //createCluster(clusterName)
        installLibrary("com.github.potix2:spark-google-spreadsheets_2.11:0.6.3", organization, clusterName)
        installLibrary("ch.cern.sparkmeasure:spark-measure_2.12:0.16", organization, clusterName)

        println("Success!")

    }

    def login(String email, String password) {
        return Browser.drive {

            go "https://community.cloud.databricks.com/login.html"
            waitFor(20) { title == "Login - Databricks Community Edition" }

            $(name: "j_username").value(email)
            assert $(name: "j_username").value() == email

            $(name: "j_password").value(password)
            assert $(name: "j_password").value() == password

            $("button", text: "Sign In").click()

            waitFor(20) { title == "Databricks Community Edition" }
        }.currentUrl.replace("https://community.cloud.databricks.com/?o=", "")

    }

    def createCluster(String clusterName) {

        Browser.drive {
            go "https://community.cloud.databricks.com/"
            waitFor(10) { title == "Databricks Community Edition" }

            $("a", text: "New Cluster").click()

            waitFor(10) { title == "Create Cluster - Databricks Community Edition" }


            waitFor { $("form").find("input", id: "cluster-input--name") }
            $("form").find("input", id: "cluster-input--name").value(clusterName)
            assert $("form").find("input", id: "cluster-input--name").value() == clusterName

            waitFor { $("input", type: "submit") }
            $("input", type: "submit").click()


            waitFor(10) { title == "Clusters - Databricks Community Edition" }
            waitFor(300) {
                $("div", text: "Running")
            }
        }
    }

    def installLibrary(String library, String organization, String clusterName) {

        Browser.drive {
            go "https://community.cloud.databricks.com/?o=" + organization + "#setting/clusters"
            waitFor(10) { title == "Clusters - Databricks Community Edition" }

            waitFor(20) { $("div", text: clusterName) }
            $("div", text: clusterName).first().click()

            waitFor { $("a", text: "Libraries") }
            $("a", text: "Libraries").click()

            int librariesCount = 0

            if ($("span", text: "Installed").displayed)
                librariesCount = $("span", text: "Installed").size()

            waitFor { $("a", text: "Install New") }
            $("a", text: "Install New").click()

            waitFor { $("button", text: "Maven") }
            $("button", text: "Maven").click()

            waitFor { $("input", name: "MAVEN.coordinates") }
            $("input", name: "MAVEN.coordinates").value(library)
            $("input", name: "MAVEN.repo").value("https://repo.maven.apache.org/maven2")
            $("span", text: "Install").click()

            waitFor(30) {
                $("span", text: "Installed").displayed &&
                        $("span", text: "Installed").size() == librariesCount + 1
            }

        }
    }

    def checkLibrary(String library, String organization, String clusterName) {

        Browser.drive {
            go "https://community.cloud.databricks.com/?o=" + organization + "#setting/clusters"
            waitFor(10) { title == "Clusters - Databricks Community Edition" }

            waitFor(20) { $("div", text: clusterName) }
            $("div", text: clusterName).first().click()

            waitFor { $("a", text: "Libraries") }
            $("a", text: "Libraries").click()
            Thread.sleep(2000)


            // <div aria-label="grid" class="ReactVirtualized__Grid ReactVirtualized__Table__Grid row-clickable" role="rowgroup" tabindex="0" style="box-sizing: border-box; direction: ltr; height: 834px; position: relative; width: 902px; will-change: transform; overflow: hidden;">
            // <div class="ReactVirtualized__Grid__innerScrollContainer" role="rowgroup" style="width: auto; height: 30px; max-width: 902px; max-height: 30px; overflow: hidden; position: relative;">
            // <div aria-rowindex="1" aria-label="row" tabindex="0" class="ReactVirtualized__Table__row" role="row" style="height: 30px; left: 0px; position: absolute; top: 0px; width: 902px; overflow: hidden; padding-right: 0px;">
            // <div aria-colindex="1" class="ReactVirtualized__Table__rowColumn" role="gridcell" style="overflow: hidden; flex: 0 1 34px;"><input type="checkbox"></div>
            // <div aria-colindex="2" class="ReactVirtualized__Table__rowColumn" role="gridcell" title="com.github.potix2:spark-google-spreadsheets_2.11:0.6.3" style="overflow: hidden; flex: 0 1 300px;">com.github.potix2:spark-google-spreadsheets_2.11:0.6.3</div>
            // <div aria-colindex="3" class="ReactVirtualized__Table__rowColumn" role="gridcell" title="Maven" style="overflow: hidden; flex: 0 1 60px;">Maven</div>
            // <div aria-colindex="4" class="ReactVirtualized__Table__rowColumn" role="gridcell" style="overflow: hidden; flex: 0 1 180px;"><span class="library-status-wrapper"><i class="library-status status-indicator-icon ok fa fa-fw fa-circle"></i>Installed</span></div>
            // <div aria-colindex="5" class="ReactVirtualized__Table__rowColumn" role="gridcell" title="" style="overflow: hidden; flex: 1 1 500px;"></div></div></div></div>
        }
    }


    static void main(String[] args) {
        InvokerHelper.runScript(DatabricksGeb, args)
    }

}
