const path = require("path");
const { CleanWebpackPlugin } = require("clean-webpack-plugin");

module.exports = {
    entry: {
        app: "./src/main/js/app.js",
        home: "./src/main/js/home.js"
    },
    plugins: [
        new CleanWebpackPlugin()
    ],
    output: {
        path: path.resolve(__dirname, "src/main/resources/static/built"),
        filename: '[name].bundle.js'
    },
    module: {
        rules: [{
            exclude: /(node_modules)/,
            use: [{
                loader: "babel-loader",
                options: {
                    presets: ["@babel/preset-env", "@babel/preset-react"]
                }
            }]
        }]
    }
};
