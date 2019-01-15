package io.github.aasaru.drools.section10;

import io.github.aasaru.drools.Common;
import io.github.aasaru.drools.domain.CustomObj;
import io.github.aasaru.drools.domain.Passport;
import io.github.aasaru.drools.repository.ApplicationRepository;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.json.JSONObject;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
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

//
//
//        System.out.println("Have you had any claims in the last 5 years? (yes / no) ");
//        String previousClaims = sc.nextLine();
//        jsonObject.put("previousClaims",previousClaims);
//        if(previousClaims.trim().equalsIgnoreCase("yes")){
//            System.out.println("How many claims (1 - 5)? ");
//            int previousClaimsCount = sc.nextInt();
//            sc.nextLine();
//            jsonObject.put("previousClaimsCount",previousClaimsCount);
//        }
//        System.out.println("Are you a seasonal Business?(yes / no) ");
//        String isSeasonal = sc.nextLine();
//        jsonObject.put("isSeasonal",isSeasonal);
//
//        if(classOfBusiness.trim().equalsIgnoreCase("bakery")){
//            System.out.println("Do you have a grill/ deep fat frier ? (yes/no) ");
//            String grillOrFrier = sc.nextLine();
//            jsonObject.put("grillOrFrier",grillOrFrier);
//            if(grillOrFrier.trim().equalsIgnoreCase("yes")){
//                System.out.println("Is this a doughnut shop? (yes / no) ");
//                String isDoughnut = sc.nextLine();
//                jsonObject.put("isDoughnut",isDoughnut);
//            }
//        }
//
//        System.out.println("Do you provide delivery service ? (yes/no)");
//        String isDeliverable = sc.nextLine();
//        jsonObject.put("isDeliverable",isDeliverable);
//
//
//        // Section -3 Locations & Buildings Page
//        System.out.println("What is your annual sales ($)for this location ? (Note : no looping performed)");
//        int locationSales = sc.nextInt();
//        sc.nextLine();
//        jsonObject.put("locationSales",locationSales);
//
//        System.out.println("What is the building limit ? ");
//        int buildingLimit = sc.nextInt();
//        sc.nextLine();
//        jsonObject.put("buildingLimit",buildingLimit);
//
//        System.out.println("Year Built ?");
//        int yearBuilt = sc.nextInt();
//        sc.nextLine();
//        jsonObject.put("yearBuilt",yearBuilt);
//
//        System.out.println("What is the distance to coast from this building? ");
//        int distanceFromCoast = sc.nextInt();
//        sc.nextLine();
//        jsonObject.put("distanceFromCoast",distanceFromCoast);
//
//        System.out.println("Do you have an alarm on premises? (yes/no)");
//        String isAlarmPresent = sc.nextLine();
//        jsonObject.put("isAlarmPresent",isAlarmPresent);
//
//        System.out.println("Are there other safety features? (yes/no)");
//        String otherSafetyFeatures = sc.nextLine();
//        jsonObject.put("otherSafetyFeatures",otherSafetyFeatures);

        // custom object created
        System.out.println("response  ::=>" );
        System.out.println(jsonObject);
        CustomObj responseObject = new CustomObj(jsonObject);
        return responseObject;

    }


    public void localDroolsSession(){
        StatefulBakery sb= new StatefulBakery();
        CustomObj responseObject = sb.gatherAnswers();
        KieContainer kieClasspathContainer = KieServices.Factory.get().getKieClasspathContainer();
        KieSession ksession = kieClasspathContainer.newKieSession("Statefulbakery" );
        List<CustomObj> facts = new ArrayList<>();
        facts.add(responseObject);
        facts.forEach(ksession::insert);
        //        List<Passport> passports = ApplicationRepository.getPassports();
        //        passports.forEach(ksession::insert);
        System.out.println("==== DROOLS SESSION START ==== ");
        ksession.fireAllRules();
        ksession.dispose();
        System.out.println("==== DROOLS SESSION END ==== ");

        System.out.println("==== PASSPORTS AFTER DROOLS SESSION === ");
//        passports.forEach(passport -> {
//        System.out.println(passport + " verdict: " + passport.getValidation() + ", cause: " + passport.getCause());
//        })
    }

    public KieContainer getKieContainer() {

        String drlURL = "https://s3.amazonaws.com/dsci-4/rules.drl";
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
//        kieFileSystem.write(ResourceFactory.newClassPathResource(premiumDrl + ".drl"));
        kieFileSystem.write(ResourceFactory.newClassPathResource(drlURL));
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();
        KieModule kieModule = kieBuilder.getKieModule();
        return kieServices.newKieContainer(kieModule.getReleaseId());
    }


    public void droolsSessionFromS3(){
        StatefulBakery sb= new StatefulBakery();
        CustomObj responseObject = sb.gatherAnswers();
        KieContainer kieClasspathContainer = getKieContainer();
        KieSession ksession = kieClasspathContainer.newKieSession("S3Statefulbakery" );
        List<CustomObj> facts = new ArrayList<>();
        facts.add(responseObject);
        System.out.println(facts);
        facts.forEach(ksession::insert);
        System.out.println("ksession ####################### ");
        System.out.println(ksession);
        //        List<Passport> passports = ApplicationRepository.getPassports();
        //        passports.forEach(ksession::insert);
        System.out.println("==== DROOLS SESSION START ==== ");
        ksession.fireAllRules();
        ksession.dispose();
        System.out.println("==== DROOLS SESSION END ==== ");

        System.out.println("==== PASSPORTS AFTER DROOLS SESSION === ");
//        passports.forEach(passport -> {
//        System.out.println(passport + " verdict: " + passport.getValidation() + ", cause: " + passport.getCause());
//        })


    }





    public static void main(final String[] args) {
        StatefulBakery sb= new StatefulBakery();
//        sb.localDroolsSession();
        sb.droolsSessionFromS3();
    }









}
