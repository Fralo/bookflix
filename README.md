this is the backend of a microscopic saas called bookflix, its only purpose is to generate a use case for the `easyj` framework (that lives inside this repo).

the `easyj` framework is the core of this project, it aims to be a Django/Laravel like framework written in Java (with a more than naive implementation)

docker build . -t java-test-server && docker run --rm -p8000:8000 java-test-server
