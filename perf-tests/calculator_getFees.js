// 1. init code (once per VU)
// prepares the script: loading files, importing modules, and defining functions

import { check } from 'k6';
import { SharedArray } from 'k6/data';
import { getFeesByPsp, getFees } from './helpers/calculator_helper.js';

// read configuration
// note: SharedArray can currently only be constructed inside init code
// according to https://k6.io/docs/javascript-api/k6-data/sharedarray
const varsArray = new SharedArray('vars', function () {
	return JSON.parse(open(`./${__ENV.VARS}`)).environment;
});
// workaround to use shared array (only array should be used)
const vars = varsArray[0];
const rootUrl = `${vars.host}`;

export const options = {
	discardResponseBodies: true,
	scenarios: {
		// load_scenario: {
		// 	// name of the executor to use
		// 	executor: 'per-vu-iterations',
		//
		// 	// common scenario configuration
		// 	startTime: '10s',
		// 	gracefulStop: '5s',
		//
		// 	// executor-specific configuration
		// 	vus: 2,
		// 	iterations: 2,
		// 	maxDuration: '10s',
		// },
		rampingVus: {
			executor: 'ramping-vus',
			startVUs: 0,
			stages: [
				{ duration: '3s', target: 30000 },
				{ duration: '10s', target: 30000 },
				{ duration: '5s', target: 75000 },
				{ duration: '5s', target: 50000 },
				{ duration: '5s', target: 100000 },
				{ duration: '10s', target: 50000 },
				{ duration: '15s', target: 450000 },
				{ duration: '25s', target: 150000 },
				{ duration: '15s', target: 200000 },
				{ duration: '20s', target: 50000 },
				{ duration: '10s', target: 30000 },
				{ duration: '5s', target: 0 },
			],
			gracefulRampDown: '0s',
		}
	},
};

export default function calculator() {


	const params = {
		headers: {
			'Content-Type': 'application/json',
		},
	};

	const payload = {
		"paymentAmount": 70,
		"primaryCreditorInstitution": "fiscalCode-1",
		"paymentMethod": "CP",
		"touchpoint": "CHECKOUT",
		"idPspList": [],
		"transferList": [
			{

				"creditorInstitution": "fiscalCode-1",
				"transferCategory": "TAX1"
			},
			{
				"creditorInstitution": "fiscalCode-2",
				"transferCategory": "TAX2"
			}
		]
	}
    response = getFees(rootUrl, payload, params);
	check(response, {
		'getFees': (r) => r.status === 200,
	});

}


export function handleSummary(data) {
	return { 'raw-data.json': JSON.stringify(data)};
}
