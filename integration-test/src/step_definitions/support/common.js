const axios = require("axios");

axios.defaults.headers.common['Ocp-Apim-Subscription-Key'] = process.env.SUBKEY // for all requests
if (process.env.canary) {
  axios.defaults.headers.common['X-CANARY'] = 'canary' // for all requests
}

function get(url, headers) {
  return axios.get(url, { headers })
    .then(res => {
      return res;
    })
    .catch(error => {
      return error.response;
    });
}

function post(url, body, headers) {
  return axios.post(url, body, { headers })
    .then(res => {
      return res;
    })
    .catch(error => {
      return error.response;
    });
}

function put(url, body, headers) {
  return axios.put(url, body, { headers })
    .then(res => {
      return res;
    })
    .catch(error => {
	console.log("***** error", error)
      return error.response;
    });
}

function del(url, headers) {
  return axios.delete(url, { headers })
    .then(res => {
      return res;
    })
    .catch(error => {
      return error.response;
    });
}

function call(method, url, body) {
  if (method === 'GET') {
    return get(url)
  }
  if (method === 'POST') {
    return post(url, body)
  }
  if (method === 'PUT') {
    return put(url, body)
  }
  if (method === 'DELETE') {
    return del(url)
  }

}

function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

module.exports = { get, post, put, del, call, sleep }
