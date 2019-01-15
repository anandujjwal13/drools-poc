package io.github.aasaru.drools.section10;

import io.github.aasaru.drools.Common;
import io.github.aasaru.drools.domain.CustomObj;
import io.github.aasaru.drools.domain.Passport;
import io.github.aasaru.drools.repository.ApplicationRepository;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.json.JSONObject;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import sun.jvm.hotspot.debugger.Page;

import java.io.InputStream;
import java.net.URL;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StatefulBakery {

    public CustomObj gatherAnswers(){
        Scanner sc = new Scanner(System.in);

        // Section 1 Business Details Page

        JSONObject jsonObject = new JSONObject();
        System.out.println("What is the class of your business? (bakery) ");
        String classOfBusiness = sc.nextLine();
        jsonObject.put("classOfBusiness",classOfBusiness);
        System.out.println("Which year did you start this business");
        int age = sc.nextInt();
        sc.nextLine();
        jsonObject.put("age",age);

        //  Section 2  UnderWriting Questions Page

        System.out.println("Have you had any claims in the last 5 years? (yes / no) ");
        String previousClaims = sc.nextLine();
        jsonObject.put("previousClaims",previousClaims);
        if(previousClaims.trim().equalsIgnoreCase("yes")){
            System.out.println("How many claims (1 - 5)? ");
            int previousClaimsCount = sc.nextInt();
            sc.nextLine();
            jsonObject.put("previousClaimsCount",previousClaimsCount);
        }
        System.out.println("Are you a seasonal Business?(yes / no) ");
        String isSeasonal = sc.nextLine();
        jsonObject.put("isSeasonal",isSeasonal);

        if(classOfBusiness.trim().equalsIgnoreCase("bakery")){
            System.out.println("Do you have a grill/ deep fat frier ? (yes/no) ");
            String grillOrFrier = sc.nextLine();
            jsonObject.put("grillOrFrier",grillOrFrier);
            if(grillOrFrier.trim().equalsIgnoreCase("yes")){
                System.out.println("Is this a doughnut shop? (yes / no) ");
                String isDoughnut = sc.nextLine();
                jsonObject.put("isDoughnut",isDoughnut);
            }
        }

        System.out.println("Do you provide delivery service ? (yes/no)");
        String isDeliverable = sc.nextLine();
        jsonObject.put("isDeliverable",isDeliverable);


        // Section -3 Locations & Buildings Page
        System.out.println("What is your annual sales ($)for this location ? (Note : no looping performed)");
        int locationSales = sc.nextInt();
        sc.nextLine();
        jsonObject.put("locationSales",locationSales);

        System.out.println("What is the building limit ? ");
        int buildingLimit = sc.nextInt();
        sc.nextLine();
        jsonObject.put("buildingLimit",buildingLimit);

        System.out.println("Year Built ?");
        int yearBuilt = sc.nextInt();
        sc.nextLine();
        jsonObject.put("yearBuilt",yearBuilt);

        System.out.println("What is the distance to coast from this building? ");
        int distanceFromCoast = sc.nextInt();
        sc.nextLine();
        jsonObject.put("distanceFromCoast",distanceFromCoast);

        System.out.println("Do you have an alarm on premises? (yes/no)");
        String isAlarmPresent = sc.nextLine();
        jsonObject.put("isAlarmPresent",isAlarmPresent);

        System.out.println("Are there other safety features? (yes/no)");
        String otherSafetyFeatures = sc.nextLine();
        jsonObject.put("otherSafetyFeatures",otherSafetyFeatures);

        // custom object created
        System.out.println("response Object :: => " );
        System.out.println(jsonObject);
        CustomObj responseObject = new CustomObj(jsonObject);
        return responseObject;

    }


    public void localDroolsSession(){
        StatefulBakery sb= new StatefulBakery();
        CustomObj responseObject = sb.gatherAnswers();
        KieContainer kieClasspathContainer = KieServices.Factory.get().getKieClasspathContainer();
        KieSession ksession = kieClasspathContainer.newKieSession("Statefulbakery" );

        // For insertion of multiple facts
        // List<CustomObj> facts = new ArrayList<>();
        // facts.add(responseObject);
        // facts.forEach(ksession::insert);

        ksession.insert(responseObject);
        System.out.println("==== DROOLS SESSION START (Rule files in Local File system are loaded) ==== ");
        ksession.fireAllRules();
        ksession.dispose();
        System.out.println("==== DROOLS SESSION END ==== ");
    }

    public KieContainer getKieContainer(String drlURL) {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        kieFileSystem.write(ResourceFactory.newUrlResource(drlURL));
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();
        KieModule kieModule = kieBuilder.getKieModule();
        return kieServices.newKieContainer(kieModule.getReleaseId());
    }


    public void droolsSessionFromS3(){
        String drlURL = "https://s3.amazonaws.com/dsci-4/rules.drl";
        CustomObj responseObject = gatherAnswers();
        KieContainer myKieContainer = getKieContainer(drlURL);
        KieBase kieBase = myKieContainer.getKieBase();
        KieSession myKieSession = kieBase.newKieSession();
        myKieSession.insert(responseObject);
        System.out.println("==== DROOLS SESSION START (Rule files stored in AWS S3 system are loaded) ==== ");
        myKieSession.fireAllRules();
        myKieSession.dispose();
        System.out.println("==== DROOLS SESSION END ==== ");
    }

    public static void main(final String[] args) {
        System.out.println("Enter : ");
        System.out.println("0 to run rules stored in local");
        System.out.println("1 to run rules stored in S3");
        Scanner sc = new Scanner(System.in);
        int localOrRemote = sc.nextInt();
        sc.nextLine();
        StatefulBakery sb= new StatefulBakery();
        if(localOrRemote == 0){
            sb.localDroolsSession();
        }
        else if(localOrRemote == 1){
            sb.droolsSessionFromS3();
        }
        else {
            System.out.println("No such option Available");
        }
    }

}
