import http from 'k6/http';
import { sleep, check } from 'k6';

export const options = {
  vus: 5,
  duration: '60s'
};

export default function () {
  const res = http.get('http://localhost:8081/personas/reserve');
  check(res, {
    'status is 200': (r) => r.status === 200,
    'has id': (r) => JSON.parse(r.body).id !== undefined,
  });
  sleep(0.2);
}
