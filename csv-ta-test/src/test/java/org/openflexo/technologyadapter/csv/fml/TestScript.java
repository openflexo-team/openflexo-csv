

package org.openflexo.technologyadapter.csv.fml;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openflexo.foundation.fml.cli.ParseException;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.ta.csv.CSVTechnologyAdapter;

import java.io.IOException;


public class TestScript {

    private static AutomatedTests automatedTests;

    
    @BeforeClass
    public static void setUp() throws Exception {
        System.out.println("========================================");
        System.out.println("Initializing Test Infrastructure");
        System.out.println("========================================");

        
        
        automatedTests = new AutomatedTests(null, "SpecificScriptRunner");

        System.out.println("Available scripts:");
        System.out.println(AutomatedTests.listAvailableScripts());
        System.out.println("========================================\n");
    }

    
    @Test
    public void testLoadCSVScript() throws Exception {
        System.out.println("\n>>> Running TestLoadCSV.fmlscript\n");
        automatedTests.runSpecificScript("TestLoadCSV.fmlscript");
    }

    
    @Test
    public void testCSVBasicsScript() throws Exception {
        System.out.println("\n>>> Running TestCSVBasics.fmlscript\n");
        automatedTests.runSpecificScript("TestCSVBasics.fmlscript");
    }

    
    @Test
    public void testCSVVMScript() throws Exception {
        System.out.println("\n>>> Running TestCSVVM.fmlscript\n");
        automatedTests.runSpecificScript("TestCSVVM.fmlscript");
    }


    @Test
    public void testPrinterModel() throws Exception {
        System.out.println("\n>>> Running TestPrinterModel.fmlscript\n");
        automatedTests.runSpecificScript("TestPrinterModel.fmlscript");
    }
     @Test
        public void testPrinterModelWithCycleDetection() throws Exception {
            System.out.println("\n>>> Running TestCycleDetection.fmlscript\n");
            automatedTests.runSpecificScript("TestCycleDetection.fmlscript");
        }

    
    @Test
    public void testMultipleScripts() throws Exception {
        System.out.println("\n>>> Running multiple scripts in sequence\n");

        automatedTests.runSpecificScripts(
                "TestLoadCSV.fmlscript",
                "TestCSVBasics.fmlscript",
                "TestCSVVM.fmlscript"
        );

        System.out.println("\nAll scripts completed successfully!");
    }

    
    @Test
    public void testListScripts() {
        System.out.println("\n>>> Available FML Scripts:\n");
        System.out.println(AutomatedTests.listAvailableScripts());
    }
    

}