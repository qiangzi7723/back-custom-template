pipeline {
    agent any

    tools {
        maven 'Custom-Maven'
    }

    stages {
        stage('Build') {
            steps {
                echo '开始构建'
                sh 'mvn package'
            }
        }

        stage('Deploy') {
            steps {
                echo '打包镜像'
                sh 'docker login --username=广州威纳信息科技有限公司 --password=via@#123 registry.cn-shenzhen.aliyuncs.com'
                script {
                        def name = 'via-custom'
                        def registry = 'https://registry.cn-shenzhen.aliyuncs.com'
                        def namespace = 'via-tech'
                        echo "Deploying to ${name} in ${registry}"


                        docker.withRegistry(registry) {
                            def image = docker.build("${namespace}/${name}:latest")
                            image.push('latest')
                        }
                    }
                }

            }

    }


    post {
      always {
        echo '构建完成'
      }
    }
}
