const { Given, When, Then, BeforeAll, AfterAll, setDefaultTimeout } = require('@cucumber/cucumber')

const assert = require("assert");
const { call, post, sleep } = require("./common");
const fs = require("fs");
const dataStoreClient = require("./datastore_client");


const afm_utils_host = process.env.AFM_UTILS_HOST;

/*increased the default timeout of the promise to allow 
the correct execution of the smoke tests*/
setDefaultTimeout(15000);

let responseToCheck;
let cdis = [];
let urlDeleteBundlesByIdCDI;
let idTouchPoint = "id-touchpoint-wisp";



// Synchronous
BeforeAll(async function() {
  dataStoreClient.setup("touchpoints", idTouchPoint, "WISP", "WISP")
});

Given('the configuration {string}', async function(filePath) {
  let file = fs.readFileSync('./config/' + filePath);
  cdis = JSON.parse(file);
  let result = await post(afm_utils_host + '/cdis/sync',
    cdis);
  assert.strictEqual(result.status, 200);
});

Given(/^the URL to delete bundles by idCDI$/, function() {
  urlDeleteBundlesByIdCDI = "/psps/" + cdis[0].idPsp + "/cdis/" + cdis[0].idCdi;
});

When(/^the client call the (GET|POST|PUT|DELETE) API$/,
  async function(method) {
    responseToCheck = await call(method, afm_utils_host + urlDeleteBundlesByIdCDI);
  });

Then(/^check statusCode is (\d+)$/, function(status) {
  assert.strictEqual(responseToCheck.status, status);
});



// Asynchronous Promise
AfterAll(async function() {
  await sleep(1000);
  responseToCheck = await dataStoreClient.deleteDocument("touchpoints", idTouchPoint, "WISP");
  assert.strictEqual(responseToCheck.status, 204);
  return Promise.resolve()
});