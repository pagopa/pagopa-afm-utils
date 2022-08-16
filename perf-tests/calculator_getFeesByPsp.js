// 1. init code (once per VU)
// prepares the script: loading files, importing modules, and defining functions

import { check } from 'k6';
import { SharedArray } from 'k6/data';
import { getFeesByPsp, getFees } from './helpers/calculator_helper.js';

// read configuration
// note: SharedArray can currently only be constructed inside init code
// according to https://k6.io/docs/javascript-api/k6-data/sharedarray
const varsArray = new SharedArray('vars', function () {
	const data = JSON.parse(open(`./${__ENV.VARS}`))
	return [data.environment[0], data.rampingVus];
});
// workaround to use shared array (only array should be used)
const vars = varsArray[0];
const optsConfiguration = varsArray[1];
const rootUrl = `${vars.host}`;

export const options = {
	discardResponseBodies: true,
	scenarios: {
		rampingVus: optsConfiguration,
		// rampingVus: {
		// 	executor: 'ramping-vus',
		// 	startVUs: 0,
		// 	stages: [
		// 		{ duration: '3s', target: 30000 },
		// 		{ duration: '10s', target: 30000 },
		// 		{ duration: '5s', target: 75000 },
		// 		{ duration: '5s', target: 50000 },
		// 		{ duration: '5s', target: 100000 },
		// 		{ duration: '10s', target: 50000 },
		// 		{ duration: '15s', target: 450000 },
		// 		{ duration: '25s', target: 150000 },
		// 		{ duration: '15s', target: 200000 },
		// 		{ duration: '20s', target: 50000 },
		// 		{ duration: '10s', target: 30000 },
		// 		{ duration: '5s', target: 0 },
		// 	],
		// 	gracefulRampDown: '0s',
		// }
	},
};

export default function calculator_getFeesByPsp() {

	const params = {
		headers: {
			'Content-Type': 'application/json',
		},
	};

	let payload = {
        "paymentAmount": 70,
        "primaryCreditorInstitution": "fiscalCode-1",
        "paymentMethod": "CP",
        "touchpoint": "CHECKOUT",
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
    };

    const idPsp = String(10).padStart(11, '0');
	let response = getFeesByPsp(rootUrl, idPsp, payload, params);
	check(response, {
		'getFeesByPsp': (r) => r.status === 200,
	});
}


export function handleSummary(data) {
	return { 'raw-data.json': JSON.stringify(data)};
}
