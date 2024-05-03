const path = require('path');
const { VueLoaderPlugin } = require('vue-loader');

module.exports = {
  entry: './src/main/vue/main.js', // Update to point to your main JavaScript file
  output: {
    path: path.resolve(__dirname, 'src/main/webapp'),
    filename: 'bundle.js'
  },
  module: {
    rules: [
      {
        test: /\.vue$/,
        loader: 'vue-loader'
      },
      {
        test: /\.css$/,
        use: ['style-loader', 'css-loader']
      },
      // Add other loaders if needed
    ]
  },
  resolve: {
    extensions: ['.js', '.vue'], // Add .vue extension to resolve Vue components
    alias: {
      '@': path.resolve(__dirname, 'src/main/vue') // Alias to src/main/vue directory
    }
  },
  plugins: [
    new VueLoaderPlugin()
  ]
};
