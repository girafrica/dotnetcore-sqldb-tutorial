import java.text.SimpleDateFormat
library 'shared'

pipeline {
    agent any
    environment {
        CONFIG = 'cmp-stg'
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
                echo("Foo: ${env.CONFIG}")
            }
        }

        stage('Checkout2') {
            steps {
                script {
                    setupEnvironment()
                    checkoutCode()              
                    }                
                }            
            }

        stage('Create version') {
            steps {
                script {
                    currentDateTime = sh script: """
                        date +"v%Y.%V"
                        """.trim(), returnStdout: true
                    version = currentDateTime.trim()  // the .trim() is necessary
                    echo "version: " + version
                }
            }
        }    

        stage('List tags') {
            steps {
                script {
                  int x = 1;
  
                  //lastTag = sh script: """git tag --list ${version}* --sort=-version:refname | head -1 | grep -oE '[0-9]+\044'""".trim(), returnStdout: true
                  lastTag = sh script: """git tag --sort=-version:refname | head -1 | grep -oE '[0-9]+\044'""".trim(), returnStdout: true

                  lt = lastTag.trim()  // the .trim() is necessary
                  echo "lastTag: " + lt
                  int lt = lt.toInteger()

                  newtag = lt + x

                  echo newtag.toString()
                }
            }
        }  

        stage('Create tag') {
            steps {
                script {
                    createTag(version, newtag)            
                }
            }
        }
    }
}