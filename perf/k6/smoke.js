import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 20 }, // Ramp up to 20 users
    { duration: '1m', target: 20 },  // Stay at 20 users
    { duration: '30s', target: 0 },  // Ramp down to 0 users
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'], // 95% of requests should be below 500ms
    http_req_failed: ['rate<0.01'],   // Less than 1% of requests should fail
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export default function () {
  const credentials = Buffer.from('admin:admin').toString('base64');
  const headers = {
    'Authorization': `Basic ${credentials}`,
    'Content-Type': 'application/json',
  };

  // Create a test run
  const runPayload = {
    name: 'Performance Test Run',
    description: 'Created by k6 performance test',
  };

  const runResponse = http.post(`${BASE_URL}/api/runs`, JSON.stringify(runPayload), {
    headers: headers,
  });

  check(runResponse, {
    'create run status is 201': (r) => r.status === 201,
  });

  if (runResponse.status === 201) {
    const runId = runResponse.json('id');

    // Get run details
    const getRunResponse = http.get(`${BASE_URL}/api/runs/${runId}`, {
      headers: headers,
    });

    check(getRunResponse, {
      'get run status is 200': (r) => r.status === 200,
      'run name is correct': (r) => r.json('name') === runPayload.name,
    });
  }

  sleep(1);
}
