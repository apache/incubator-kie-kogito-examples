# Loan Broker JS

Loan Broker UI based on Typescript, NextJS and RecoilJS

## Dependencies

NodeJS v12.22.12+
Yarn 1.22.19+

## How to prepare the code to start using it?

Execute the following commands:

```bash
yarn install # Install all dependencies

yarn dev # Run the Loan Broker JS in development mode at http://localhost:3000

yarn build # Build and optimize the application.
yarn start # Run the Loan Broker JS in prod mode with optimizations at http://localhost:3000
```

## Where to change Env variables?

If you need to run the application and change where is the `LOANBROKER WORKFLOW URL` or the `WEBSOCKET URL` you need to do it on the `.env` files. You may need to change those properties in the `.env.development` since that will be used when you use the `yarn dev` command. The `.env.production` contains the properties that will be used inside the Kubernetes cluster and or when the `yarn start` command is executed. Please try not to change the `.env.production` unless you know what you are doing.

## Where is the application?

The application is hosted in http://localhost:3000 for your local environment. If you run inside the Kubernetes cluster please check the documentation in the root directory.
