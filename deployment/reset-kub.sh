#!/usr/bin/env bash
set -e

echo "🔄 Resetting AcademiaConnect deployment in Kubernetes..."

# Delete the namespace (which removes all associated resources)
echo "Deleting namespace 'AcademiaConnect'..."
kubectl delete namespace AcademiaConnect --ignore-not-found=true

# Wait until the namespace is fully deleted
echo "Waiting for namespace 'AcademiaConnect' to be removed..."
while kubectl get namespace AcademiaConnect >/dev/null 2>&1; do
  echo "Namespace 'AcademiaConnect' still exists, waiting..."
  sleep 5
done

echo "Namespace 'AcademiaConnect' deleted."

# Redeploy all resources using the deploy.sh script
# echo "Redeploying all resources..."
# ./deploy.sh

echo "✅ AcademiaConnect Kubernetes reset complete."