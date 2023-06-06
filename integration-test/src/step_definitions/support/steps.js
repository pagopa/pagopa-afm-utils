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



// Synchronous
BeforeAll(function() {
  dataStoreClient.setupTestTouchPoints("touchpoints", "id-touchpoint-wisp", "WISP", "WISP");
  dataStoreClient.setupTestTouchPoints("touchpoints", "id-touchpoint-io", "IO", "IO");
  dataStoreClient.setupTestTouchPoints("touchpoints", "id-touchpoint-checkout", "CHECKOUT", "CHECKOUT");
  // prior cancellation to avoid dirty cases --> the idPsp is the one in the test ./config/cdis.json file
  dataStoreClient.deleteTestBundles("bundles", "IDPSPINTTEST01", "IDPSPINTTEST01");
});

Given('the configuration {string}', async function(filePath) {
  await sleep(1000);
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
    await sleep(1000);
    responseToCheck = await call(method, afm_utils_host + urlDeleteBundlesByIdCDI);
  });

Then(/^check statusCode is (\d+)$/, function(status) {
  assert.strictEqual(responseToCheck.status, status);
});



// Asynchronous Promise
AfterAll(async function() {
  await sleep(1000);
  dataStoreClient.deleteTestTouchPoints("touchpoints", "WISP", "WISP");
  dataStoreClient.deleteTestTouchPoints("touchpoints", "IO", "IO");
  dataStoreClient.deleteTestTouchPoints("touchpoints", "CHECKOUT", "CHECKOUT");
  // the idPsp is the one in the test ./config/cdis.json file
  dataStoreClient.deleteTestBundles("bundles", "IDPSPINTTEST01", "IDPSPINTTEST01");
  return Promise.resolve()
});