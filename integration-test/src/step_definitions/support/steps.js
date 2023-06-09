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
});

Given('the configuration {string}', async function(filePath) {
  // prior cancellation to avoid dirty cases --> the idPsp is the one in the test ./config/cdis.json file
  await dataStoreClient.deleteTestDataByIdPsp("bundles", "IDPSPINTTEST01", "IDPSPINTTEST01");
  let file = fs.readFileSync('./config/' + filePath);
  cdis = JSON.parse(file);
  let result = await post(afm_utils_host + '/cdis/sync',
    cdis);
  assert.strictEqual(result.status, 200);
});

Given(/^the URL to delete bundles by idCDI$/, function() {
  urlDeleteBundlesByIdCDI = "/psps/" + cdis[0].idPsp + "/cdis/" + cdis[0].idCdi;
});

Given('the URL to delete bundles by the non-existent idCDI {string}', function(idCdi) {
  urlDeleteBundlesByIdCDI = "/psps/" + cdis[0].idPsp + "/cdis/" + idCdi;
});

When(/^the client call the (GET|POST|PUT|DELETE) API$/,
  async function(method) {
    await sleep(2000);
    responseToCheck = await call(method, afm_utils_host + urlDeleteBundlesByIdCDI);
  });

Then(/^check statusCode is (\d+)$/, function(status) {
  assert.strictEqual(responseToCheck.status, status);
});

// Asynchronous Promise
AfterAll(async function() {
  await dataStoreClient.deleteTestTouchPoints("touchpoints", "WISP", "WISP");
  await dataStoreClient.deleteTestTouchPoints("touchpoints", "IO", "IO");
  await dataStoreClient.deleteTestTouchPoints("touchpoints", "CHECKOUT", "CHECKOUT");
  // the idPsp is the one in the test ./config/cdis.json file
  await dataStoreClient.deleteTestDataByIdPsp("bundles", "IDPSPINTTEST01", "IDPSPINTTEST01");
  await dataStoreClient.deleteTestDataByIdPsp("cdis", "IDPSPINTTEST01", "IDPSPINTTEST01");
  return Promise.resolve()
});