package com.kansas.TaigaAPI.utils;

import com.kansas.TaigaAPI.Main;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GlobalData {


    public static String getTaigaURL() {
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return "default_taiga_url";
            }

            Properties properties = new Properties();
            properties.load(input);
            return properties.getProperty("TAIGA_API_ENDPOINT");
        } catch (IOException e) {
            e.printStackTrace();
            return "default_taiga_url";
        }
    }


    public static String getBurndownURL() {
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return "default_taiga_url";
            }

            Properties properties = new Properties();
            properties.load(input);

            return properties.getProperty("BURNDOWN_URL");
        } catch (IOException e) {
            e.printStackTrace();
            return "http://localhost:8081";
        }
    }

    public static String getCycletimeURL() {
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return "default_taiga_url";
            }

            Properties properties = new Properties();
            properties.load(input);
            return properties.getProperty("CYCLETIME_URL");
        } catch (IOException e) {
            e.printStackTrace();
            return "http://localhost:8070";
        }
    }

    public static String getVelocityURL() {
      try (InputStream input = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return "default_taiga_url";
            }

            Properties properties = new Properties();
            properties.load(input);
            return properties.getProperty("VELOCITY_URL");
        } catch (IOException e) {
            e.printStackTrace();
            return "http://localhost:8090";
        }
    }

    public static String getLeadTimeURL() {
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
              if (input == null) {
                  System.out.println("Sorry, unable to find config.properties");
                  return "default_taiga_url";
              }
  
              Properties properties = new Properties();
              properties.load(input);
              return properties.getProperty("LEADTIME_URL");
          } catch (IOException e) {
              e.printStackTrace();
              return "http://localhost:8090";
          }
      }
    
      public static String getEstimateEffectivenessURL() {
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
              if (input == null) {
                  System.out.println("Sorry, unable to find config.properties");
                  return "default_taiga_url";
              }
  
              Properties properties = new Properties();
              properties.load(input);
              return properties.getProperty("ESTIMATEEFFECTIVENESS_URL");
          } catch (IOException e) {
              e.printStackTrace();
              return "http://localhost:8090";
          }
      }

      //getAUC_URL

    public static String getAUC_URL() {
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
              if (input == null) {
                  System.out.println("Sorry, unable to find config.properties");
                  return "default_taiga_url";
              }
  
              Properties properties = new Properties();
              properties.load(input);
              return properties.getProperty("AUC_URL");
          } catch (IOException e) {
              e.printStackTrace();
              return "http://localhost:8090";
          }
      }


      //vip microservice
        public static String getVipURL() {
            try (InputStream input = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
                  if (input == null) {
                        System.out.println("Sorry, unable to find config.properties");
                        return "default_taiga_url";
                    }
        
                    Properties properties = new Properties();
                    properties.load(input);
                    return properties.getProperty("VIP_URL");

                } catch (IOException e) {
                    e.printStackTrace();
                    return "http://localhost:8090";
                }
        }

    public static String getValueAucURL() {
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return "default_taiga_url";
            }

            Properties properties = new Properties();
            properties.load(input);
            return properties.getProperty("VALUE_AUC_URL");

        } catch (IOException e) {
            e.printStackTrace();
            return "http://localhost:8090";
        }
    }
    }
